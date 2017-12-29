package com.speedcentral.hm.server.core

import java.nio.file.Path

sealed trait RecordingResult
case class RecordingSuccess(recordingId: String, stdout: String, stderr: String, outputVideo: Path) extends RecordingResult
case class RecordingFailure(recordingId: String, stdout: String, stderr: String) extends RecordingResult
