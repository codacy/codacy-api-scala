package com.codacy.api.helpers.vcs

import java.io.File
import java.util.Date

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{Repository, RepositoryBuilder}

import scala.collection.JavaConverters._
import scala.util.Try

case class CommitInfo(uuid: String, authorName: String, authorEmail: String, date: Date)

class GitClient(workDirectory: File) {

  val repository: Try[Repository] = Try(new RepositoryBuilder().findGitDir(workDirectory).readEnvironment().build())

  def latestCommitUuid(): Option[String] = {
    repository.map { rep =>
      val git = new Git(rep)
      val headRev = git.log().setMaxCount(1).call().asScala.head
      headRev.getName
    }.toOption.filter(_.trim.nonEmpty)
  }

  def latestCommitInfo: Try[CommitInfo] = {
    repository.map { rep =>
      val git = new Git(rep)
      val headRev = git.log().setMaxCount(1).call().asScala.head
      val authorIdent = headRev.getAuthorIdent

      CommitInfo(headRev.getName, authorIdent.getName, authorIdent.getEmailAddress, authorIdent.getWhen)
    }
  }

}
