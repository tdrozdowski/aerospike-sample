name := """aerospike-sample"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  cache,
  ws,
  "eu.unicredit" %% "reactive-aerospike" % "0.2.0-SNAPSHOT"
)
