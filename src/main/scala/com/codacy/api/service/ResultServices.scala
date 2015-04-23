package com.codacy.api.service

import com.codacy.api.ResultReport
import com.codacy.api.client.{CodacyClient,  Request, RequestResponse, RequestSuccess}
import play.api.libs.json.Json

class ResultServices(client: CodacyClient) {

  def sendResults(commitUuid: String, resultReport: ResultReport): RequestResponse[RequestSuccess] = {
    val endpoint = s"results/$commitUuid"

    val value = Json.toJson(resultReport).toString()

    client.post(Request(endpoint, classOf[RequestSuccess]), value)
  }

}
