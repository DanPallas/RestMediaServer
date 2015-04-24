lazy val commonSettings = Seq(
  organization := "org.restmediaserver",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.5",
  libraryDependencies += "commons-io" % "commons-io" % "2.4",
  libraryDependencies += "org" % "jaudiotagger" % "2.0.3",
  libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2",
  libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.3.9"
)

lazy val RestMediaServer = (project in file(".")).
  aggregate(Core).
  settings(commonSettings: _*)

lazy val Core = (project in file("Core")).
  settings(commonSettings: _*)
