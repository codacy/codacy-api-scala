package com.codacy.api.helpers

import java.io.{File, PrintWriter}

import com.codacy.api.helpers.vcs.GitClient
import play.api.libs.json.{Format, Json}

import scala.io.{Codec, Source}

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
                   (block: (String) => Either[String, T]): Either[String, T] = {
    val gitClient = new GitClient(currentPath)

    commitUUID.orElse(gitClient.latestCommitUuid()).map { currentCommitUUID =>
      block(currentCommitUUID)
    }.getOrElse {
      Left("could not retrieve the current commit uuid")
    }
  }

  def readJsonFromFile[A](file: File)(implicit fmt: Format[A]): Option[A] = {
    val source = Source.fromFile(file)(Codec.UTF8)
    val lines = try {
      source.mkString
    } finally source.close()
    Json.parse(lines).asOpt[A]
  }

  def writeJsonToFile[A](file: File, value: A)(implicit fmt: Format[A]): Boolean = {
    val reportJson = Json.toJson(value).toString()
    val printWriter = new PrintWriter(file)
    val result = util.Try(printWriter.write(reportJson)).isSuccess
    printWriter.close()
    result
  }
}
