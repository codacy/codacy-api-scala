import sbt._

resolvers ++= Seq(
  "bintray repos" at "https://dl.bintray.com/sbt/sbt-plugin-releases",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
   Resolver.typesafeRepo("releases")
)

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.2-1")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.2.1")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")
