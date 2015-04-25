package com.codacy.api.helpers

import java.io.File
import java.nio.file.Paths
import java.util.UUID

import com.codacy.api.ResultReport
import com.codacy.api.client.CodacyClient
import com.codacy.api.service.ResultServices
import org.eclipse.jgit.util.FileUtils

object ResultHelper {

  private val currentPath = Paths.get("").toAbsolutePath
  private val outputDirectory = new File(currentPath.toFile, "target/codacy-report")

  private def readReports(commitUUID: String): Seq[ResultReport] = {
    val allReports = outputDirectory.list.toSeq.flatMap { file =>
      val reportFile = new File(outputDirectory, file)
      FileHelper.readJsonFromFile[ResultReport](reportFile).filter(_.commitUUID == commitUUID)
    }

    allReports.groupBy(_.algorithmUUID).map { case (algoUUID, algoReports) =>
      val results = algoReports.map(_.results).reduceOption(_ ++ _).getOrElse(Seq.empty)
      val distinctResults = results.distinct
      ResultReport(algoUUID, commitUUID, distinctResults)
    }.toSeq
  }

  def writeReportToFile(report: ResultReport): Either[String, Boolean] = {
    outputDirectory.mkdirs()
    val randomUUID = UUID.randomUUID().toString
    val outputFile = new File(outputDirectory, s"codacy-report-$randomUUID.json")
    Right(FileHelper.writeJsonToFile(outputFile, report))
  }

  def sendReport(apiUrl: Option[String] = None, projectTokenOpt: Option[String] = None,
                 commitUUIDOpt: Option[String] = None): Either[String, Boolean] = {
    FileHelper.withTokenAndCommit(projectTokenOpt, commitUUIDOpt) {
      case (projectToken, commitUUID) =>
        val codacyClient = new CodacyClient(apiUrl = apiUrl, projectToken = Some(projectToken))
        val resultServices = new ResultServices(codacyClient)

        val requestResponses = readReports(commitUUID).map { resultReport =>
          val response = resultServices.sendResults(commitUUID, resultReport)
          response match {
            case requestResponse if requestResponse.hasError => Left(requestResponse.message)
            case _ => Right(true)
          }
        }

        // TODO: Probably shouldn't be deleted. Useful for debug. Create a clean task instead.
        FileUtils.delete(outputDirectory, FileUtils.RECURSIVE + FileUtils.IGNORE_ERRORS)

        requestResponses
          .collectFirst { case Left(message) => Left(message) }
          .getOrElse(Right(true))
    }
  }

}
