lazy val commonSettings = Seq(
  organization := "org.restmediaserver",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.5"
)

lazy val RestMediaServer = (project in file(".")).
  aggregate(Core, MediaScanner).
  settings(commonSettings: _*)

lazy val Core = (project in file("Core")).
  settings(commonSettings: _*)
  
lazy val MediaScanner = (project in file("MediaScanner")).
  dependsOn(Core).
  settings(commonSettings: _*)
