package com.codacy.api.service

import com.codacy.api.client.{CodacyClient, Request, RequestResponse, RequestSuccess}
import com.codacy.api.CoverageReport
import play.api.libs.json.Json

class CoverageServices(client: CodacyClient) {

  def sendReport(
      commitUuid: String,
      language: String,
      coverageReport: CoverageReport,
      partial: Boolean = false
  ): RequestResponse[RequestSuccess] = {
    val endpoint = s"coverage/$commitUuid/${language.toLowerCase}"

    PostRequest(endpoint, coverageReport, partial)
  }

  def sendFinalNotification(commitUuid: String): RequestResponse[RequestSuccess] = {
    val endpoint = s"commit/$commitUuid/coverageFinal"

    PostEmptyRequest(endpoint)
  }

  def sendReportWithProjectName(
      username: String,
      projectName: String,
      commitUuid: String,
      language: String,
      coverageReport: CoverageReport,
      partial: Boolean = false
  ): RequestResponse[RequestSuccess] = {
    val endpoint = s"$username/$projectName/commit/$commitUuid/coverage/${language.toLowerCase}"
    PostRequest(endpoint, coverageReport, partial)
  }

  def sendFinalWithProjectName(
      username: String,
      projectName: String,
      commitUuid: String
  ): RequestResponse[RequestSuccess] = {
    val endpoint = s"$username/$projectName/commit/$commitUuid/coverageFinal"

    PostEmptyRequest(endpoint)
  }

  private def PostRequest(endpoint: String, coverageReport: CoverageReport, partial: Boolean) = {
    val queryParams = getQueryParameters(partial)

    val jsonString = serializeCoverageReport(coverageReport)

    client.post(Request(endpoint, classOf[RequestSuccess], queryParams), jsonString)
  }

  private def PostEmptyRequest(endpoint: String) =
    client.post(Request(endpoint, classOf[RequestSuccess]), "{}")

  private def getQueryParameters(partial: Boolean) = {
    Map("partial" -> partial.toString)
  }
  private def serializeCoverageReport(coverageReport: CoverageReport) =
    Json.stringify(Json.toJson(coverageReport))
}
