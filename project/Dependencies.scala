import sbt._

object Dependencies {

  // Generic
  val jodaTime = "joda-time" % "joda-time" % "2.7"
  val jodaConvert = "org.joda" % "joda-convert" % "1.8.1" //to fix jodaTime in scala 2.10
  val jgit = "org.eclipse.jgit" % "org.eclipse.jgit" % "3.6.2.201501210735-r"

  val `rapture-net` = "com.propensive" %% "rapture-net" % "2.0.0-M8" withSources()
  val playJson = "com.typesafe.play" %% "play-json" % "2.6.10" withSources()

  // Test
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"


}
