import Dependencies._
import com.amazonaws.auth.{EnvironmentVariableCredentialsProvider, SystemPropertiesCredentialsProvider}
import com.amazonaws.services.s3.model.Region
import ohnosequences.sbt.SbtS3Resolver._
import sbt.Keys._
import sbt._

name := """codacy-api-scala"""

version := "1.0.0"

scalaVersion := "2.10.5"

crossScalaVersions := Seq("2.10.5", "2.11.6")

scalacOptions := Seq("-deprecation", "-feature", "-unchecked", "-Ywarn-adapted-args", "-Xlint")

resolvers += "Typesafe maven repository" at "http://repo.typesafe.com/typesafe/maven-releases/"

libraryDependencies ++= Seq(
  jodaTime,
  playWS,
  jgit
)

organization := "com.codacy"

organizationName := "Codacy"

S3Resolver.defaults

s3region := Region.EU_Ireland

s3credentials := {
  file(System.getProperty("user.home")) / ".sbt" / ".s3credentials" |
    new EnvironmentVariableCredentialsProvider() |
    new SystemPropertiesCredentialsProvider() |
    file(".s3credentials")
}

s3overwrite := true

publishMavenStyle := true

publishTo := Some(s3resolver.value("Cenas releases S3 bucket publish", s3("releases.mvn-repo.cenas.com")).withMavenPatterns)

resolvers ++= Seq[Resolver](s3resolver.value("Cenas releases S3 bucket", s3("releases.mvn-repo.cenas.com")).withMavenPatterns)
