package com.codacy.api.helpers

import java.io.File
import java.util.UUID

import com.codacy.api.ResultReport
import com.codacy.api.client.{CodacyClient, FailedResponse, SuccessfulResponse}
import com.codacy.api.service.ResultServices

class ResultHelper {

  private val currentPath = new File(System.getProperty("user.dir"))
  private val defaultOutputDirectory = new File(currentPath, "target/codacy-reports")

  def writeReportToFile(report: ResultReport, outputDirectory: Option[File] = None): Boolean = {
    val directory = outputDirectory.getOrElse(defaultOutputDirectory)
    directory.mkdirs()
    val randomUUID = UUID.randomUUID().toString
    val outputFile = new File(directory, s"codacy-report-$randomUUID.json")
    FileHelper.writeJsonToFile(outputFile, report)
  }

  def sendReport(
      outputDirectories: Option[Seq[File]] = None,
      apiUrl: Option[String] = None,
      projectTokenOpt: Option[String] = None,
      commitUUIDOpt: Option[String] = None
  ): Either[String, Boolean] = {

    FileHelper.withTokenAndCommit(projectTokenOpt, commitUUIDOpt) {
      case (projectToken, commitUUID) =>
        val codacyClient = new CodacyClient(apiUrl = apiUrl, projectToken = Some(projectToken))
        val resultServices = new ResultServices(codacyClient)

        val requestResponses = readReports(commitUUID, outputDirectories).map { resultReport =>
          val response = resultServices.sendResults(commitUUID, resultReport)
          response match {
            case FailedResponse(message) => Left(message)
            case _: SuccessfulResponse[_] => Right(true)
          }
        }

        requestResponses
          .collectFirst { case Left(message) => Left(message) }
          .getOrElse(Right(true))
    }
  }

  private def readReports(commitUUID: String, outputDirectories: Option[Seq[File]]): Seq[ResultReport] = {
    val directories = outputDirectories.getOrElse(Seq(defaultOutputDirectory))

    val allReports = directories.flatMap { directory =>
      directory.list.toSeq.flatMap { file =>
        val reportFile = new File(directory, file)
        FileHelper.readJsonFromFile[ResultReport](reportFile).filter(_.commitUUID == commitUUID)
      }
    }

    allReports
      .groupBy(_.algorithmUUID)
      .map {
        case (algoUUID, algoReports) =>
          val results = algoReports.map(_.results).reduceOption(_ ++ _).getOrElse(Seq.empty)
          val distinctResults = results.distinct
          ResultReport(algoUUID, commitUUID, distinctResults)
      }
      .toSeq
  }

}
