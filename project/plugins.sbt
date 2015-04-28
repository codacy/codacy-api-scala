import sbt._

resolvers ++= Seq(
  DefaultMavenRepository,
  "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Typesafe maven repository" at "https://repo.typesafe.com/typesafe/maven-releases/",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  Classpaths.typesafeReleases,
  Classpaths.sbtPluginReleases
)

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.3")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.8")
