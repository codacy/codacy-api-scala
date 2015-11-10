import sbt._

resolvers ++= Seq(
  "bintray repos" at "https://dl.bintray.com/sbt/sbt-plugin-releases",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.5.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.9")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.3")

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.2.1")
