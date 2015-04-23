import sbt._

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Era7 maven releases" at "http://releases.era7.com.s3.amazonaws.com"
)

// Codacy private repo
addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.12.0")
