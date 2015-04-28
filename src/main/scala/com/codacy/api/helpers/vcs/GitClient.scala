package com.codacy.api.helpers.vcs

import java.io.File

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder

import scala.collection.JavaConversions._
import scala.util.Try

class GitClient(cwd: File) {

  val repository = Try(new RepositoryBuilder().findGitDir(cwd).readEnvironment().build()).toOption

  def latestCommitUuid(): Option[String] = {
    Try {
      repository.map { rep =>
        val git = new Git(rep)
        val headRev = git.log().setMaxCount(1).call().head
        headRev.getName
      }
    }.toOption.flatten
  }

}
