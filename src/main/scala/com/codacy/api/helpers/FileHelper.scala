package com.codacy.api.helpers

import java.io.{File, PrintWriter}

import com.codacy.api.helpers.vcs.GitClient
import play.api.libs.json._

import scala.io.{Codec, Source}
import scala.util.Try

object FileHelper {

  private val currentPath = new File(System.getProperty("user.dir"))

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
                   (block: String => Either[String, T]): Either[String, T] = {
    val gitClient = new GitClient(currentPath)

    commitUUID.orElse(gitClient.latestCommitUuid()).map { currentCommitUUID =>
      block(currentCommitUUID)
    }.getOrElse {
      Left("could not retrieve the current commit uuid")
    }
  }

  def readJsonFromFile[A](file: File)(implicit reads: Reads[A]): Option[A] = {
    val source = Source.fromFile(file)(Codec.UTF8)
    val lines = try {
      source.mkString
    } finally source.close()
    Json.parse(lines).validate[A].fold(
      _ => Option.empty[A],
      derived => Option(derived)
    )
  }

  def writeJsonToFile[A](file: File, value: A)(implicit writes: Writes[A]): Boolean = {
    val reportJson = Json.stringify(Json.toJson(value))
    val printWriter = new PrintWriter(file)
    val result = Try(printWriter.write(reportJson)).isSuccess
    printWriter.close()
    result
  }

}
