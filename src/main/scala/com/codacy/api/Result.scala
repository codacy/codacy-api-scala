package com.codacy.api

case class ResultReport(algorithmUUID: String, commitUUID: String, results: Seq[Result])

case class Result(rule: String, filePath: String, line: Int, message: String)
