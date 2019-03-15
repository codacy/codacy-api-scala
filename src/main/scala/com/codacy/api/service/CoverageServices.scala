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
    val queryParams = Map("partial" -> partial.toString)

    val jsonString = Json.stringify(Json.toJson(coverageReport))

    client.post(Request(endpoint, classOf[RequestSuccess], queryParams), jsonString)
  }

  def sendFinalNotification(commitUuid: String): RequestResponse[RequestSuccess] = {
    val endpoint = s"commit/$commitUuid/coverageFinal"

    client.post(Request(endpoint, classOf[RequestSuccess]), "{}")
  }

}
