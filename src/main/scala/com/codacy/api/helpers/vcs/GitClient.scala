package com.codacy.api.helpers.vcs

import java.io.File

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{Repository, RepositoryBuilder}

import scala.collection.JavaConversions._
import scala.util.Try

class GitClient(workDirectory: File) {

  val repository: Option[Repository] = Try(new RepositoryBuilder().findGitDir(workDirectory).readEnvironment().build()).toOption

  def latestCommitUuid(): Option[String] = {
    Try {
      repository.map { rep =>
        val git = new Git(rep)
        val headRev = git.log().setMaxCount(1).call().head
        headRev.getName
      }
    }.toOption.flatten.filter(_.trim.nonEmpty)
  }

}
