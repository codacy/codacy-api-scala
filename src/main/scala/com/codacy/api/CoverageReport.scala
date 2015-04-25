package com.codacy.api

import play.api.libs.json._

case class CoverageFileReport(filename: String, total: Int, coverage: Map[Int, Int])

case class CoverageReport(language: Language.Value, total: Int, fileReports: Seq[CoverageFileReport])

object CoverageReport {

  val intMapReads: Reads[Map[Int, Int]] = Reads { (json: JsValue) =>
    json match {
      case JsObject(fields) => JsSuccess(
        fields.collect {
          case (key, JsNumber(value)) =>
            key.toInt -> value.toInt
        }.toMap
      )
      case _ => JsError()
    }
  }

  val intMapWrites: Writes[Map[Int, Int]] = Writes { (intMap: Map[Int, Int]) =>
    val stringMap = intMap.map {
      case (key, value) =>
        key.toString -> value
    }

    Json.toJson(stringMap)
  }

  implicit val intMapFmt: Format[Map[Int, Int]] = Format(intMapReads, intMapWrites)

  implicit val codacyCoverageFileReportFmt: Format[CoverageFileReport] = Json.format[CoverageFileReport]
  implicit val codacyCoverageReportFmt: Format[CoverageReport] = Json.format[CoverageReport]

}
