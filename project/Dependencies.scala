import sbt._

object Dependencies {

  // Generic
  //play-functional from play-json depends on joda-time which only compiles if joda-convert is explicitly added :/
  val jodaConvert = "org.joda" % "joda-convert" % "2.1.2" 
  val jgit = "org.eclipse.jgit" % "org.eclipse.jgit" % "3.6.2.201501210735-r"
  val scalajHttp = "org.scalaj" %% "scalaj-http" % "2.4.1"
  val playJson = "com.typesafe.play" %% "play-json" % "2.6.10" withSources()

  // Test
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"


}
