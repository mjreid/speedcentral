import sbt._

object Dependencies {
  private val akkaHttpVersion = "10.0.11"
  private val akkaVersion = "2.4.20"
  private val slf4jVersion = "1.7.25"
  private val logbackVersion = "1.2.3"
  private val scalaTestVersion = "3.0.4"
  private val scalaMockVersion = "3.6.0"

  val slf4j: ModuleID = "org.slf4j" % "slf4j-api" % slf4jVersion
  val akkaSlf4j: ModuleID = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val logback: ModuleID = "ch.qos.logback" % "logback-classic" % logbackVersion

  val akkaHttp: ModuleID = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val sprayJson: ModuleID = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  val scalaMock: ModuleID = "org.scalamock" %% "scalamock-scalatest-support" % scalaMockVersion % "test"


  val all = Seq(slf4j, akkaHttp, sprayJson, scalaTest, scalaMock, logback, akkaSlf4j)

  // API-only dependencies; should be lightweight
  val api = Seq(sprayJson, scalaTest)
}