import sbt._

object Dependencies {

  // Generic
  lazy val jodaTime = "joda-time"          % "joda-time"        % "2.7"
  lazy val jgit     = "org.eclipse.jgit"   % "org.eclipse.jgit" % "3.6.2.201501210735-r"

  // Test
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"

  // Play framework
  lazy val playWS   = "com.typesafe.play" %% "play-ws"          % "2.3.9"

}
