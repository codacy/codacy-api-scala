import sbt._

object Dependencies {

  val raptureVersion = "2.0.0-M7"

  // Generic
  val jodaTime = "joda-time" % "joda-time" % "2.7"
  val jgit = "org.eclipse.jgit" % "org.eclipse.jgit" % "3.6.2.201501210735-r"
  val `rapture-json` = "com.propensive" %% "rapture-json" % raptureVersion withSources()
  val `rapture-net` = "com.propensive" %% "rapture-net" % raptureVersion withSources()

  // Test
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  val `rapture-json-play` = "com.propensive" %% "rapture-json-play" % raptureVersion % "test"

}
