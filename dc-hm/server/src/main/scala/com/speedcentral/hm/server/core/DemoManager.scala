package com.speedcentral.hm.server.core

import java.util.Base64

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.speedcentral.hm.api.DemoRequest
import com.speedcentral.hm.server.core.RecordingManager.BeginRecording
import com.speedcentral.hm.server.core.UploadManager.UploadVideo

class DemoManager(
  databaseManager: ActorRef,
  recordingManager: ActorRef,
  uploadManager: ActorRef
) extends Actor with ActorLogging {

  import DemoManager._

  override def receive: Receive = {
    case StartDemo(demoRequest) =>
      log.info(s"Received demo request for ${demoRequest.demoMetadata.runId}")
      val lmpDataBytes = Base64.getDecoder.decode(demoRequest.lmpData)
      recordingManager ! BeginRecording(demoRequest.demoMetadata.runId, lmpDataBytes)

    case RecordingComplete(recordingResult) =>
      recordingResult match {
        case RecordingFailure(runId, _, _) =>
          // Do something here eventually
          log.error(s"Recording failure for $runId")
        case RecordingSuccess(runId, _, _, outputVideo) =>
          log.info(s"Recording succeeded for $runId")
          uploadManager ! UploadVideo(runId, outputVideo)
      }

    case UploadStarted(info) =>
      log.info(s"Upload started: $info")

    case UploadCompleted(info) =>
      log.info(s"Work complete! $info")
  }
}

object DemoManager {
  def props(databaseManager: ActorRef, recordingManager: ActorRef, uploadManager: ActorRef): Props =
    Props(new DemoManager(databaseManager, recordingManager, uploadManager))

  case class StartDemo(demoRequest: DemoRequest)

  case class RecordingComplete(recordingResult: RecordingResult)

  case class UploadStarted(info: UploadStartedInfo)

  case class UploadCompleted(info: UploadCompletedInfo)
}


