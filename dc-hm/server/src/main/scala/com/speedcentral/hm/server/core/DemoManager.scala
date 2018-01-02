package com.speedcentral.hm.server.core

import java.util.Base64

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.speedcentral.hm.server.core.DatabaseManager._
import com.speedcentral.hm.server.core.RecordingManager.BeginRecording
import com.speedcentral.hm.server.core.UploadManager.UploadVideo

class DemoManager(
  databaseManager: ActorRef,
  recordingManager: ActorRef,
  uploadManager: ActorRef
) extends Actor with ActorLogging {

  import DemoManager._

  override def receive: Receive = {
    case StartDemo(recordingId, lmpData) =>
      log.info(s"Received demo request for $recordingId")
      val lmpDataBytes = Base64.getDecoder.decode(lmpData)
      recordingManager ! BeginRecording(recordingId, lmpDataBytes)

    case RecordingStarted(recordingId) =>
      databaseManager ! LogExeRecordingStarted(recordingId)

    case RecordingComplete(recordingResult) =>
      recordingResult match {
        case RecordingFailure(recordingId, _, _) =>
          // Do something here eventually
          log.error(s"Recording failure for $recordingId")
          databaseManager ! LogExeRecordingFailed(recordingId)
        case RecordingSuccess(recordingId, _, _, outputVideo) =>
          log.info(s"Recording succeeded for $recordingId")
          databaseManager ! LogExeRecordingSucceeded(recordingId)
          uploadManager ! UploadVideo(recordingId, outputVideo)
      }

    case UploadStarted(info) =>
      log.info(s"Upload started: $info")
      databaseManager ! LogUploadStarted(info.recordingId, info.videoId)

    case UploadFailed(recordingId, error) =>
      log.info(s"Upload failed: $error")
      databaseManager ! LogUploadFailed(recordingId, error)

    case UploadSucceeded(recordingId) =>
      log.info(s"Upload succeeded for $recordingId")
      databaseManager ! LogUploadSucceeded(recordingId)
  }
}

object DemoManager {
  def props(databaseManager: ActorRef, recordingManager: ActorRef, uploadManager: ActorRef): Props =
    Props(new DemoManager(databaseManager, recordingManager, uploadManager))

  case class StartDemo(recordingId: String, lmpData: String)

  case class RecordingStarted(recordingId: String)

  case class RecordingComplete(recordingResult: RecordingResult)

  case class UploadStarted(info: UploadStartedInfo)

  case class UploadFailed(recordingId: String, error: String)

  case class UploadSucceeded(recordingId: String)
}


