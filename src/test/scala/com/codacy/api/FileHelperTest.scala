package com.codacy.api

import com.codacy.api.helpers.FileHelper
import org.scalatest.{FlatSpec, Matchers}

class FileHelperTest extends FlatSpec with Matchers {

  "FileHelper" should "withTokenAndCommit" in {

    val result: Either[String, Boolean] = FileHelper.withTokenAndCommit(projectToken = Some("PROJECT-TOKEN-DUMMY-VALUE")) {
      case (projectToken, commitUUID) =>

        projectToken shouldNot be('empty)
        commitUUID shouldNot be('empty)

        Right(true)
    }

    result should be('right)

    result match {
      case Right(r) => r shouldBe (true)

      case Left(s) => fail(s"Should not produce a Left. Left value: $s")
    }
  }

  it should "withCommit" in {

    val result: Either[String, String] = FileHelper.withCommit() {
      case currentCommitUUID =>
        Right(currentCommitUUID)
    }

    result should be('right)

    result match {
      case Right(r) => r shouldNot be('empty)

      case Left(s) => fail(s"Should not produce a Left. Left value: $s")
    }
  }
}
