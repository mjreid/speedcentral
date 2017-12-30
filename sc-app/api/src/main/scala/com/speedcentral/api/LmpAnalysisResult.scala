package com.speedcentral.api

case class LmpAnalysisResult(
  episode: Option[Int],
  map: Option[Int],
  skillLevel: Option[Int],
  engineVersion: Option[String],
  iwad: Option[String],
  pwads: Seq[String]
)