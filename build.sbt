lazy val commonSettings = Seq(
  organization := "org.restmediaserver",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.5"
)

lazy val RestMediaServer = (project in file(".")).
  aggregate(DomainObjects, MediaSync).
  settings(commonSettings: _*)

lazy val DomainObjects = (project in file("DomainObjects")).
  settings(commonSettings: _*)
  
lazy val MediaSync = (project in file("MediaSync")).
  dependsOn(DomainObjects).
  settings(commonSettings: _*)
