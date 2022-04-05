import codacy.libs._

name := "codacy-api-scala"

scalaVersion := "2.12.15"
scalacOptions := Seq("-deprecation", "-feature", "-unchecked", "-Ywarn-adapted-args", "-Xlint", "-Xfatal-warnings")

// Runtime dependencies
libraryDependencies ++= Seq(jodaConvert, jgit, scalajHttp, Dependencies.playJson)

// Test dependencies
libraryDependencies ++= Seq(scalatest).map(_ % "test")

// HACK: Since we are only using the public resolvers we need to remove the private for it to not fail
resolvers ~= {
  _.filterNot(_.name.toLowerCase.contains("codacy"))
}

// HACK: This setting is not picked up properly from the plugin
pgpPassphrase := Option(System.getenv("SONATYPE_GPG_PASSPHRASE")).map(_.toCharArray)

description := "Client for Codacy API"

scmInfo := Some(
  ScmInfo(url("https://github.com/codacy/codacy-api-scala"), "scm:git:git@github.com:codacy/codacy-api-scala.git")
)

publicMvnPublish

fork in Test := true
cancelable in Global := true
