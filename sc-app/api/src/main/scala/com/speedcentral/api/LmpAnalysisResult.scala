package com.speedcentral.api

case class LmpAnalysisResult(
  episode: Option[Int],
  map: Option[Int],
  skillLevel: Option[Int],
  engineVersion: Option[String],
  iwad: String,
  primaryPwad: Option[Pwad],
  secondaryPwads: Seq[Pwad]
)