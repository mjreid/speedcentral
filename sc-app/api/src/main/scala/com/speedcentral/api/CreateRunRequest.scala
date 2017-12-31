package com.speedcentral.api

case class CreateRunRequest(
  map: Int,
  episode: Int,
  skillLevel: Int,
  iwad: String,
  engineVersion: String,
  runner: Option[String],
  submitter: Option[String],
  category: Option[String],
  runTime: Option[String], // ISO 8601 duration,
  lmpBytes: Array[Byte]
)