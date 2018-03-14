package com.codacy.api.client

import rapture.codec.encodings.`UTF-8`._
import rapture.data.Parser
import rapture.io._
import rapture.json._
import rapture.net._

class CodacyClient(apiUrl: Option[String] = None, apiToken: Option[String] = None,
                   projectToken: Option[String] = None)
                  (implicit astParser: Parser[String, JsonAst]) {

  private val tokens = Map.empty[String, String] ++
    apiToken.map(t => "api_token" -> t) ++
    projectToken.map(t => "project_token" -> t)

  private val remoteUrl = apiUrl.getOrElse("https://api.codacy.com") + "/2.0"

  /*
   * Does an API request and parses the json output into a class
   */
  def execute[T](request: Request[T])(implicit extractor: Extractor[T, Json]): RequestResponse[T] = {
    get(request.endpoint) match {
      case SuccessfulResponse(json) => SuccessfulResponse(json.as[T])
      case f: FailedResponse => f
    }
  }

  /*
   * Does an paginated API request and parses the json output into a sequence of classes
   */
  def executePaginated[T](request: Request[Seq[T]])(implicit extractor: Extractor[T, Json]): RequestResponse[Seq[T]] = {
    get(request.endpoint) match {
      case SuccessfulResponse(json) =>
        val nextPage = (json \ "next").as[Option[String]]
        val nextRepos = nextPage.map {
          nextUrl =>
            executePaginated(Request(nextUrl, request.classType))
        }.getOrElse(SuccessfulResponse(Seq.empty))

        val values = (json \ "values").as[List[T]]
        //.fold[RequestResponse[List[T]]](FailedResponse(s"Failed to parse json: $json"))(a => SuccessfulResponse(a))
        RequestResponse.apply(SuccessfulResponse(values), nextRepos)

      case f: FailedResponse => f
    }
  }

  /*
   * Does an API post
   */
  def post[T](request: Request[T], value: String)(implicit extractor: Extractor[T, Json]): RequestResponse[T] = {
    val headers = tokens ++ Map("Content-Type" -> "application/json")

    val body = Http.parse(s"$remoteUrl/${request.endpoint}")
      .httpPost(value, headers)
      .slurp[Char]

    parseJson(body) match {
      case SuccessfulResponse(json) => SuccessfulResponse(json.as[T])
      case failure: FailedResponse => failure
    }
  }

  private def get(endpoint: String): RequestResponse[Json] = {
    val headers = tokens ++ Map("Content-Type" -> "application/json")

    val body = Http.parse(s"$remoteUrl/$endpoint")
      .httpGet(headers)
      .slurp[Char]

    parseJson(body)
  }

  private def parseJson(input: String): RequestResponse[Json] = {
    import rapture.core.modes.returnResult._

    val raptureResult = for {
      json <- Json.parse(input)
    } yield {
      (json \ "error").as[Option[String]]
        .getOrElse(None)
        .fold[RequestResponse[Json]](
        SuccessfulResponse(json))(
        error => FailedResponse(error)
      )
    }

    raptureResult
      .fold(identity, { errors =>
        val msgs = errors.map { case (_, (_, error)) => error.getMessage }.mkString("\n")
        FailedResponse(apiResponseParseError(msgs, input))
      })
  }

  private def apiResponseParseError(errorMsg: String, responseBody: String) = {
    s"""Failed to parse API response:
       | $responseBody
       | With the following errors:
       | $errorMsg
        """.stripMargin
  }
}
