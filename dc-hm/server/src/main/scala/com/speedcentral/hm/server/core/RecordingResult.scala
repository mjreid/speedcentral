package com.speedcentral.hm.server.core

sealed trait RecordingResult
case class RecordingSuccess(runId: String, stdout: String, stderr: String) extends RecordingResult
case class RecordingFailure(runId: String, stdout: String, stderr: String) extends RecordingResult
