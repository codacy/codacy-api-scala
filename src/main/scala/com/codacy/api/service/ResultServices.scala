package com.codacy.api.service

import com.codacy.api.ResultReport
import com.codacy.api.client.{CodacyClient, Request, RequestResponse, RequestSuccess, RequestTimeout}
import play.api.libs.json.Json

class ResultServices(client: CodacyClient) {

  def sendResults(
      commitUuid: String,
      resultReport: ResultReport,
      timeoutOpt: Option[RequestTimeout] = None,
      sleepTime: Option[Int] = None,
      noRetries: Option[Int] = None
  ): RequestResponse[RequestSuccess] = {
    val endpoint = s"/commit/$commitUuid/results"

    val value = Json.stringify(Json.toJson(resultReport))

    client.post(Request(endpoint, classOf[RequestSuccess]), value, timeoutOpt, sleepTime, noRetries)
  }

}
