package com.codacy.api

import play.api.libs.json.{Json, Format}

case class Result(rule: String, filePath: String, line: Int, message: String)

case class ResultReport(algorithmUUID: String, commitUUID: String, results: Seq[Result])

object ResultReport {

  implicit val resultFormatter: Format[Result] = Json.format[Result]
  implicit val resultReportFormatter: Format[ResultReport] = Json.format[ResultReport]
}
