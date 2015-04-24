package com.codacy.api.service

import com.codacy.api.CoverageReport
import com.codacy.api.client.{CodacyClient, Request, RequestResponse, RequestSuccess}
import play.api.libs.json.Json

class CoverageServices(client: CodacyClient) {

  def sendReport(commitUuid: String, coverageReport: CoverageReport): RequestResponse[RequestSuccess] = {
    val endpoint = s"coverage/$commitUuid"

    val value = Json.toJson(coverageReport).toString()

    client.post(Request(endpoint, classOf[RequestSuccess]), value)
  }

}
