name := "dc-hm"

lazy val commonSettings = Seq(
  organization := "com.github.mjreid",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.12",
  test in assembly := {}
)

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .settings(publish := { })
  .settings(publishArtifact := false)
  .disablePlugins(sbtassembly.AssemblyPlugin)
  .aggregate(api, server)


lazy val api = project.in(file("api"))
  .settings(commonSettings)
  .settings(name := "DcHmApi")
  .settings(libraryDependencies ++= Dependencies.api)
  .disablePlugins(sbtassembly.AssemblyPlugin)

lazy val server = project.in(file("server"))
  .settings(commonSettings)
  .settings(name := "DcHmServer")
  .settings(libraryDependencies ++= Dependencies.all)
  .settings(mainClass in assembly := Some("com.speedcentral.hm.server.Server"))
  .dependsOn(api)
