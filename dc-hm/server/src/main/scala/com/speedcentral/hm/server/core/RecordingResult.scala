package com.speedcentral.hm.server.core

import java.nio.file.Path

sealed trait RecordingResult
case class RecordingSuccess(runId: String, stdout: String, stderr: String, outputVideo: Path) extends RecordingResult
case class RecordingFailure(runId: String, stdout: String, stderr: String) extends RecordingResult
