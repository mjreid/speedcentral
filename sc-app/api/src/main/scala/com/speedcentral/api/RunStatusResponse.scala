package com.speedcentral.api

case class RunStatusResponse(
  run: ApiRun
)

case class ApiRun(
  runId: String,
  map: String,
  episode: String,
  skillLevel: String,
  iwad: String,
  engineVersion: String,
  runner: Option[String],
  submitter: Option[String],
  runCategory: Option[String],
  runTime: Option[String], // ISO 8601 duration
  createdDate: String, // ISO 8601
  modifiedDate: String, // ISO 8601
  recordings: Seq[ApiRecording]
)

case class ApiRecording(
  recordingId: String,
  videoId: Option[String],
  history: Seq[ApiRecordingHistory]
)

case class ApiRecordingHistory(
  recordingHistoryId: String,
  state: String,
  historyTime: String, // ISO 8601
  details: Option[String]
)