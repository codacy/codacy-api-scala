package com.codacy.api

import com.codacy.api.helpers.ResultHelper
import java.io.File
import java.nio.file.Files
import org.scalatest._

class ResultHelperTest extends FlatSpec with Matchers {

  "ResultHelper" should "writeReportToFile" in {

    val algorithmUUID: String = "algorithmUUID"
    val commitUUID: String = "commitUUID"

    val rule = "rule"
    val filePath = "filePath"
    val line = 1
    val message = "message"
    val result: Result = Result(rule, filePath, line, message)

    val results: Seq[Result] = Seq(result)

    val report: ResultReport = ResultReport(algorithmUUID, commitUUID, results)
    val directory: Option[File] = Some(Files.createTempDirectory("tempDir").toFile)

    val writeResult: Boolean = new ResultHelper().writeReportToFile(report, directory)

    writeResult should be (true)
  }


}
