package com.codacy.api.helpers

import java.io.{File, PrintWriter}
import java.nio.file.Paths
import java.util.UUID

import com.codacy.api.ResultReport
import com.codacy.api.client.CodacyClient
import com.codacy.api.helpers.vcs.GitClient
import com.codacy.api.service.ResultServices
import org.eclipse.jgit.util.FileUtils
import play.api.libs.json.{Format, Json}

import scala.io.{Codec, Source}

object FileHelper {

  private val currentPath = Paths.get("").toAbsolutePath
  private val outputDirectory = new File(currentPath.toFile, "target/codacy-report")

  def writeReportToFile(report: ResultReport): Either[String, Boolean] = {
    outputDirectory.mkdirs()
    val randomUUID = UUID.randomUUID().toString
    val outputFile = new File(outputDirectory, s"codacy-report-$randomUUID.json")
    Right(writeJsonToFile(outputFile, report))
  }

  def sendReport(apiUrl: Option[String] = None, projectTokenOpt: Option[String] = None,
                 commitUUIDOpt: Option[String] = None): Either[String, Boolean] = {
    withTokenAndCommit(projectTokenOpt, commitUUIDOpt) {
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

        FileUtils.delete(outputDirectory, FileUtils.RECURSIVE + FileUtils.IGNORE_ERRORS)

        requestResponses
          .collectFirst { case Left(message) => Left(message) }
          .getOrElse(Right(true))
    }
  }

  def withTokenAndCommit[T](projectToken: Option[String] = None, commitUUID: Option[String] = None)
                           (block: (String, String) => Either[String, T]): Either[String, T] = {
    withCommit(commitUUID) { currentCommitUUID =>
      projectToken.orElse(sys.env.get("CODACY_PROJECT_TOKEN")).map { codacyProjectToken =>

        block(codacyProjectToken, currentCommitUUID)

      }.getOrElse {
        Left("could not find Codacy project token")
      }
    }
  }

  def withCommit[T](commitUUID: Option[String] = None)
                   (block: (String) => Either[String, T]): Either[String, T] = {
    val gitClient = new GitClient(currentPath)

    commitUUID.orElse(gitClient.latestCommitUuid()).map { currentCommitUUID =>
      block(currentCommitUUID)
    }.getOrElse {
      Left("could not retrieve the current commit uuid")
    }
  }

  private def readReports(commitUUID: String): Seq[ResultReport] = {
    val allReports = outputDirectory.list.toSeq.flatMap { file =>
      val reportFile = new File(outputDirectory, file)
      readJsonFromFile[ResultReport](reportFile).filter(_.commitUUID == commitUUID)
    }

    allReports.groupBy(_.algorithmUUID).map { case (algoUUID, algoReports) =>
      val results = algoReports.map(_.results).reduceOption(_ ++ _).getOrElse(Seq.empty)
      val distinctResults = results.distinct
      ResultReport(algoUUID, commitUUID, distinctResults)
    }.toSeq
  }

  private def readJsonFromFile[A](file: File)(implicit fmt: Format[A]): Option[A] = {
    val source = Source.fromFile(file)(Codec.UTF8)
    val lines = try {
      source.mkString
    } finally source.close()
    Json.parse(lines).asOpt[A]
  }

  private def writeJsonToFile[A](file: File, value: A)(implicit fmt: Format[A]) = {
    val reportJson = Json.toJson(value).toString()
    val printWriter = new PrintWriter(file)
    val result = util.Try(printWriter.write(reportJson)).isSuccess
    printWriter.close()
    result
  }
}
