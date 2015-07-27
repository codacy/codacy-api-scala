package com.codacy.api.client

import com.codacy.api.util.HTTPStatusCodes
import com.ning.http.client.AsyncHttpClient
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Await
import scala.concurrent.duration._

class CodacyClient(apiUrl: Option[String] = None, apiToken: Option[String] = None,
                   projectToken: Option[String] = None) {

  private val tokens = Seq.empty[(String, String)] ++
    apiToken.map(t => ("api_token", t)) ++
    projectToken.map(t => ("project_token", t))

  private val remoteUrl = apiUrl.getOrElse("http://localhost:9000") + "/api/2.0"

  /*
   * Does an API request and parses the json output into a class
   */
  def execute[T](request: Request[T])(implicit reader: Reads[T]): RequestResponse[T] = {
    get(request.endpoint) match {
      case Right(json) => RequestResponse(json.asOpt[T])
      case Left(error) => RequestResponse(None, error.detail, hasError = true)
    }
  }

  /*
   * Does an paginated API request and parses the json output into a sequence of classes
   */
  def executePaginated[T](request: Request[Seq[T]])(implicit reader: Reads[T]): RequestResponse[Seq[T]] = {
    get(request.endpoint) match {
      case Right(json) =>
        val nextPage = (json \ "next").asOpt[String]
        val nextRepos = nextPage.map {
          nextUrl =>
            executePaginated(Request(nextUrl, request.classType)).value.getOrElse(Seq())
        }.getOrElse(Seq())

        RequestResponse(Some((json \ "values").as[Seq[T]] ++ nextRepos))

      case Left(error) =>
        RequestResponse[Seq[T]](None, error.detail, hasError = true)
    }
  }

  /*
   * Does an API post
   */
  def post[T](request: Request[T], value: String)(implicit reader: Reads[T]): RequestResponse[T] = {
    withWSClient { client =>
      val headers = tokens ++ Seq(("Content-Type", "application/json"))

      val jpromise = client.url(s"$remoteUrl/${request.endpoint}")
        .withHeaders(headers: _*)
        .withFollowRedirects(follow = true)
        .post(value)
      val result = Await.result(jpromise, Duration(10, SECONDS))

      if (Seq(HTTPStatusCodes.OK, HTTPStatusCodes.CREATED).contains(result.status)) {
        val body = result.body

        val jsValue = parseJson(body)
        jsValue match {
          case Right(responseObj) =>
            RequestResponse(responseObj.asOpt[T])
          case Left(message) =>
            RequestResponse(None, message = message.detail, hasError = true)
        }
      } else {
        RequestResponse(None, result.statusText, hasError = true)
      }
    }
  }

  private def get(endpoint: String): Either[ResponseError, JsValue] = {
    withWSClient { client =>
      val jpromise = client.url(s"$remoteUrl/$endpoint")
        .withHeaders(tokens: _*)
        .withFollowRedirects(follow = true).get()
      val result = Await.result(jpromise, Duration(10, SECONDS))

      if (Seq(HTTPStatusCodes.OK, HTTPStatusCodes.CREATED).contains(result.status)) {
        val body = result.body

        parseJson(body)
      } else {
        Left(ResponseError(java.util.UUID.randomUUID().toString, result.statusText, result.statusText))
      }
    }
  }

  private def parseJson(input: String): Either[ResponseError, JsValue] = {
    val json = Json.parse(input)

    val errorOpt = (json \ "error").asOpt[ResponseError]

    errorOpt.map {
      error =>
        Left(error)
    }.getOrElse(Right(json))
  }

  private def withWSClient[T](block: NingWSClient => T): T = {
    val client = new NingWSClient(new AsyncHttpClient().getConfig)
    val result = block(client)
    client.close()
    result
  }
}
