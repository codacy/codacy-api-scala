package com.codacy.api

import play.api.libs.json.{JsNumber, JsObject, Json, Writes}

case class CoverageFileReport(filename: String, total: Int, coverage: Map[Int, Int])

case class CoverageReport(total: Int, fileReports: Seq[CoverageFileReport])

object CoverageReport {
  implicit val mapWrites: Writes[Map[Int, Int]] = Writes[Map[Int, Int]] { map: Map[Int, Int] =>
    JsObject(map.map {
      case (key, value) => (key.toString, JsNumber(value))
    }(collection.breakOut))
  }
  implicit val coverageFileReportWrites: Writes[CoverageFileReport] = Json.writes[CoverageFileReport]
  implicit val coverageReportWrites: Writes[CoverageReport] = Json.writes[CoverageReport]
}
