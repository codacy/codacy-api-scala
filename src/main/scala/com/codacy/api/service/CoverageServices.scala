package com.codacy.api.service

import com.codacy.api.client.{CodacyClient, Request, RequestResponse, RequestSuccess}
import com.codacy.api.{CoverageFileReport, CoverageReport, Language}
import rapture.json._
import rapture.json.formatters.compact._

class CoverageServices(client: CodacyClient)(implicit ast: JsonAst) {

  // There is something wrong with the implicit resultion.
  // As a temporary workaround this helps the compiler.
  // I declared it private so we can remove it easily when rapture fixes the issue: https://github.com/propensive/rapture/issues/238
  private implicit lazy val ser = implicitly[Serializer[CoverageFileReport,Json]]

  def sendReport(commitUuid: String, language: String, coverageReport: CoverageReport): RequestResponse[RequestSuccess] = {
    val endpoint = s"coverage/$commitUuid/${language.toLowerCase}"

    val value = Json.format(Json(coverageReport))

    client.post(Request(endpoint, classOf[RequestSuccess]), value)(implicitly)
  }

}
