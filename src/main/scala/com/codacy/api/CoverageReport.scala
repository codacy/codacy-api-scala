package com.codacy.api

import play.api.libs.json.{Json, Writes}

case class CoverageFileReport(filename: String, total: Int, coverage: Map[Int,Int])

case class CoverageReport(total: Int, fileReports: Seq[CoverageFileReport])

object CoverageReport {
  implicit val coverageFileReportWrites: Writes[CoverageFileReport] = Json.writes[CoverageFileReport]
  implicit val coverageReportWrites: Writes[CoverageReport] = Json.writes[CoverageReport]
}
