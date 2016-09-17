package com.codacy.api.service

import com.codacy.api.client.{CodacyClient, Request, RequestResponse, RequestSuccess}
import com.codacy.api.{CoverageFileReport, CoverageReport, Language}
import rapture.json._
import rapture.json.formatters.compact._

class CoverageServices(client: CodacyClient)(implicit ast: JsonAst) {

  //rafael you are right, there is something wrong with the implicit resultion
  //as a temporary workaround this helps the compiler
  //i declared it private so we can remove it easily when rapture fixes the issue
  private implicit lazy val ser = implicitly[Serializer[CoverageFileReport,Json]]

  def sendReport(commitUuid: String, language: Language.Value, coverageReport: CoverageReport): RequestResponse[RequestSuccess] = {
    val endpoint = s"coverage/$commitUuid/${language.toString.toLowerCase}"

    val value = Json.format(Json(coverageReport))

    client.post(Request(endpoint, classOf[RequestSuccess]), value)(implicitly)
  }

}
