import Dependencies._
import sbt.Keys._
import sbt._

name := """codacy-api-scala"""

version := "1.0.3-SNAPSHOT"

scalaVersion := "2.10.7"

crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.7")

scalacOptions := Seq("-deprecation", "-feature", "-unchecked", "-Ywarn-adapted-args", "-Xlint", "-Xfatal-warnings")

resolvers += "Typesafe maven repository" at "http://repo.typesafe.com/typesafe/maven-releases/"

libraryDependencies ++= Seq(
  jodaConvert,
  jgit,
  scalajHttp,
  playJson,
  scalaTest
)

organization := "com.codacy"

organizationName := "Codacy"

organizationHomepage := Some(new URL("https://www.codacy.com"))

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false}

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials("Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  sys.env.getOrElse("SONATYPE_USER", "username"),
  sys.env.getOrElse("SONATYPE_PASSWORD", "password"))

startYear := Some(2015)

description := "Scala wrapper for the Codacy API"

licenses := Seq("The MIT License (MIT)" -> url("https://raw.githubusercontent.com/codacy/codacy-api-scala/master/LICENSE"))

homepage := Some(url("http://www.github.com/codacy/codacy-api-scala/"))

pomExtra :=
  <scm>
    <url>https://github.com/codacy/sbt-codacy-coverage</url>
    <connection>scm:git:git@github.com:codacy/sbt-codacy-coverage.git</connection>
    <developerConnection>scm:git:https://github.com/codacy/sbt-codacy-coverage.git</developerConnection>
  </scm>
    <developers>
      <developer>
        <id>mrfyda</id>
        <name>Rafael</name>
        <email>rafael [at] codacy.com</email>
        <url>https://github.com/mrfyda</url>
      </developer>
    </developers>
