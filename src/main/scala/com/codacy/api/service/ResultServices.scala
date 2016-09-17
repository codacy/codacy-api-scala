package com.codacy.api.service

import com.codacy.api.ResultReport
import com.codacy.api.client.{CodacyClient, Request, RequestResponse, RequestSuccess}
import rapture.json.formatters.compact._
import rapture.json.{Json, JsonAst}

class ResultServices(client: CodacyClient)(implicit ast: JsonAst) {

  def sendResults(commitUuid: String, resultReport: ResultReport): RequestResponse[RequestSuccess] = {
    val endpoint = s"/commit/$commitUuid/results"

    val value = Json.format(Json(resultReport))

    client.post(Request(endpoint, classOf[RequestSuccess]), value)(implicitly)
  }

}
