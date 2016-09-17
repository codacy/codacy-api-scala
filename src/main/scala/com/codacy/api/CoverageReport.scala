package com.codacy.api

case class CoverageFileReport(filename: String, total: Int, coverage: Map[Int,Int])

case class CoverageReport(language: Language.Value, total: Int, fileReports: Seq[CoverageFileReport])
