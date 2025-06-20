package com.codacy.api.client

import play.api.libs.json._
import com.codacy.api.util.JsonOps
import scalaj.http.Http

import java.net.URI
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

class CodacyClient(
    apiUrl: Option[String] = None,
    apiToken: Option[String] = None,
    projectToken: Option[String] = None
) {

  private case class ErrorJson(error: String)
  private case class PaginatedResult[T](next: Option[String], values: Seq[T])

  private implicit val errorJsonFormat: Reads[ErrorJson] = Json.reads[ErrorJson]

  private val tokens = Map.empty[String, String] ++
    apiToken.map(t => "api-token" -> t) ++
    projectToken.map(t => "project-token" -> t) ++
    // This is deprecated and is kept for backward compatibility. It will removed in the context of CY-1272
    apiToken.map(t => "api_token" -> t) ++
    projectToken.map(t => "project_token" -> t)

  private val remoteUrl = URI.create(apiUrl.getOrElse("https://api.codacy.com")).resolve("/2.0").toString

  /*
   * Does an API request and parses the json output into a class
   */
  def execute[T](request: Request[T])(implicit reads: Reads[T]): RequestResponse[T] = {
    get(request.endpoint) match {
      case SuccessfulResponse(json) =>
        json
          .validate[T]
          .fold(
            errors => FailedResponse(JsonOps.handleConversionFailure(errors)),
            converted => SuccessfulResponse(converted)
          )
      case f: FailedResponse => f
    }
  }

  /*
   * Does an paginated API request and parses the json output into a sequence of classes
   */
  def executePaginated[T](request: Request[Seq[T]])(implicit reads: Reads[T]): RequestResponse[Seq[T]] = {
    implicit val paginatedResultReads: Reads[PaginatedResult[T]] = Json.reads[PaginatedResult[T]]
    get(request.endpoint) match {
      case SuccessfulResponse(json) =>
        json
          .validate[PaginatedResult[T]]
          .fold(
            errors => FailedResponse(JsonOps.handleConversionFailure(errors)), {
              case PaginatedResult(Some(nextUrl), values) =>
                val nextRepos = executePaginated(Request(nextUrl, request.classType))
                RequestResponse(SuccessfulResponse(values), nextRepos)
              case PaginatedResult(None, values) =>
                RequestResponse(SuccessfulResponse(values), SuccessfulResponse(Seq.empty))
            }
          )
      case f: FailedResponse => f
    }
  }

  /*
   * Does an API post
   */
  def post[T](
      request: Request[T],
      value: String,
      timeoutOpt: Option[RequestTimeout] = None,
      sleepTime: Option[Int],
      numRetries: Option[Int]
  )(implicit reads: Reads[T]): RequestResponse[T] = {
    val url = s"$remoteUrl/${request.endpoint}"
    try {
      val headers = tokens ++ Map("Content-Type" -> "application/json")

      val httpRequest = timeoutOpt match {
        case Some(timeout) =>
          Http(url).timeout(connTimeoutMs = timeout.connTimeoutMs, readTimeoutMs = timeout.readTimeoutMs)
        case None => Http(url)
      }

      val body = httpRequest
        .params(request.queryParameters)
        .headers(headers)
        .postData(value)
        .asString
        .body

      parseJsonAs[T](body) match {
        case failure: FailedResponse =>
          retryPost(request, value, timeoutOpt, sleepTime, numRetries.map(x => x - 1), failure.message)
        case success => success
      }
    } catch {
      case NonFatal(ex) => retryPost(request, value, timeoutOpt, sleepTime, numRetries.map(x => x - 1), ex.getMessage)
    }
  }

  private def retryPost[T](
      request: Request[T],
      value: String,
      timeoutOpt: Option[RequestTimeout],
      sleepTime: Option[Int],
      numRetries: Option[Int],
      failureMessage: String
  )(implicit reads: Reads[T]): RequestResponse[T] = {
    if (numRetries.exists(x => x > 0)) {
      sleepTime.map(x => Thread.sleep(x))
      post(request, value, timeoutOpt, sleepTime, numRetries.map(x => x - 1))
    } else {
      RequestResponse.failure(
        s"Error doing a post to $remoteUrl/${request.endpoint}: exhausted retries due to $failureMessage"
      )
    }
  }

  private def get(endpoint: String): RequestResponse[JsValue] = {
    val url = s"$remoteUrl/${endpoint}"
    try {
      val headers = tokens ++ Map("Content-Type" -> "application/json")

      val body = Http(url)
        .headers(headers)
        .asString
        .body

      parseJson(body)
    } catch {
      case NonFatal(ex) =>
        throw new Exception(s"Error doing a get to ${url}", ex)
    }
  }

  private def parseJsonAs[T](input: String)(implicit reads: Reads[T]): RequestResponse[T] = {
    parseJson(input) match {
      case failure: FailedResponse => failure
      case SuccessfulResponse(json) =>
        json
          .validate[T]
          .fold(
            errors => FailedResponse(JsonOps.handleConversionFailure(errors)),
            converted => SuccessfulResponse(converted)
          )
    }
  }

  private def parseJson(input: String): RequestResponse[JsValue] = {
    Try(Json.parse(input)) match {
      case Success(json) =>
        json
          .validate[ErrorJson]
          .fold(_ => SuccessfulResponse(json), apiError => FailedResponse(s"API Error: ${apiError.error}"))
      case Failure(exception) =>
        FailedResponse(s"Failed to parse API response as JSON: $input\nUnderlying exception - ${exception.getMessage}")
    }
  }
}
