package com.codacy.api

import play.api.libs.json._

case class ResultReport(algorithmUUID: String, commitUUID: String, results: Seq[Result])

object ResultReport {
  implicit val resultReportFmt: Format[ResultReport] = Json.format[ResultReport]
}

case class Result(rule: String, filePath: String, line: Int, message: String)

object Result {
  implicit val resultFmt: Format[Result] = Json.format[Result]
}
