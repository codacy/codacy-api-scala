package com.codacy.api.client

import rapture.net._
import rapture.io._
import rapture.codec.encodings.`UTF-8`._
import play.api.libs.json._
import play.api.libs.json.Reads._
import com.codacy.api.util.JsonOps



class CodacyClient(apiUrl: Option[String] = None, apiToken: Option[String] = None,
                   projectToken: Option[String] = None) {

  case class ErrorJson(error: String)
  implicit val errorJsonFormat: Reads[ErrorJson] = Json.reads[ErrorJson]

  case class PaginatedResult[T](next: Option[String], values: Seq[T])

  private val tokens = Map.empty[String, String] ++
    apiToken.map(t => "api_token" -> t) ++
    projectToken.map(t => "project_token" -> t)

  private val remoteUrl = apiUrl.getOrElse("https://api.codacy.com") + "/2.0"

  /*
   * Does an API request and parses the json output into a class
   */
  def execute[T](request: Request[T])(implicit reads: Reads[T]): RequestResponse[T] = {
    get(request.endpoint) match {
      case SuccessfulResponse(json) => json.validate[T].fold(
        errors => FailedResponse(JsonOps.handleDerivationFailure(errors)),
        derived => SuccessfulResponse(derived)
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
        json.validate[PaginatedResult[T]].fold(
          errors => FailedResponse(JsonOps.handleDerivationFailure(errors)),
          {
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
  def post[T](request: Request[T], value: String)(implicit reads: Reads[T]): RequestResponse[T] = {
    val headers = tokens ++ Map("Content-Type" -> "application/json")

    val body = Http.parse(s"$remoteUrl/${request.endpoint}")
      .query(request.queryParameters)
      .httpPost(value, headers)
      .slurp[Char]

    parseJsonAs[T](body)
  }

  private def get(endpoint: String): RequestResponse[JsValue] = {
    val headers = tokens ++ Map("Content-Type" -> "application/json")

    val body = Http.parse(s"$remoteUrl/$endpoint")
      .httpGet(headers)
      .slurp[Char]

    parseJson(body)
  }

  private def parseJsonAs[T](input: String)(implicit reads: Reads[T]): RequestResponse[T] = {
    parseJson(input) match {
      case failure: FailedResponse => failure
      case SuccessfulResponse(json) =>
        json.validate[T].fold(
          errors => FailedResponse(JsonOps.handleDerivationFailure(errors)),
          derived => SuccessfulResponse(derived)
        )
    }
  }

  private def parseJson(input: String): RequestResponse[JsValue] = {
    val json = Json.parse(input)
    json.validate[ErrorJson].fold(
      _ => SuccessfulResponse(json),
      apiError => FailedResponse(apiError.error)
    )
  }
}
