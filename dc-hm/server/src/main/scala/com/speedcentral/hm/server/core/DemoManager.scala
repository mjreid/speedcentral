package com.speedcentral.hm.server.core

import java.util.Base64

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.speedcentral.hm.server.core.DatabaseManager._
import com.speedcentral.hm.server.core.PwadDownloader.ResolvePwads
import com.speedcentral.hm.server.core.RecordingManager.{BeginRecording, LmpSaveSucceeded, SaveLmpToFile}
import com.speedcentral.hm.server.core.UploadManager.UploadVideo

class DemoManager(
  databaseManager: ActorRef,
  recordingManager: ActorRef,
  uploadManager: ActorRef,
  pwadDownloader: ActorRef
) extends Actor with ActorLogging {

  import DemoManager._

  override def receive: Receive = {
    case StartDemo(recordingId, lmpData) =>
      log.info(s"Received demo request for $recordingId")
      val lmpDataBytes = Base64.getDecoder.decode(lmpData)
      recordingManager ! SaveLmpToFile(recordingId, lmpDataBytes)

    case LmpSaveSucceeded(recordingId) =>
      log.info(s"LMP save succeeded, starting to resolve PWADs.")
      databaseManager ! LogPwadResolveStarted(recordingId)
      pwadDownloader ! ResolvePwads(recordingId)

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

    case PwadResolveSucceeded(recordingId) =>
      log.info(s"PWAD resolve succeeded for $recordingId")
      databaseManager ! LogPwadResolveSucceeded(recordingId)
      recordingManager ! BeginRecording(recordingId)

    case PwadResolveFailed(recordingId, error) =>
      log.error(error, s"PWAD resolve failed for $recordingId")
      val message = s"PWAD resolve failed"
      databaseManager ! LogPwadResolvedFailed(recordingId, message)

    case m @ LogPwadDownloadSucceeded(_, _, _, _) =>
      databaseManager forward m

    case m @ LogPwadDownloadFailed(_, _, _, _) =>
      databaseManager forward m
  }
}

object DemoManager {
  def props(databaseManager: ActorRef, recordingManager: ActorRef, uploadManager: ActorRef, pwadDownloader: ActorRef): Props =
    Props(new DemoManager(databaseManager, recordingManager, uploadManager, pwadDownloader))

  case class StartDemo(recordingId: String, lmpData: String)

  case class RecordingStarted(recordingId: String)

  case class RecordingComplete(recordingResult: RecordingResult)

  case class UploadStarted(info: UploadStartedInfo)

  case class UploadFailed(recordingId: String, error: String)

  case class UploadSucceeded(recordingId: String)

  case class PwadResolveSucceeded(recordingId: String)

  case class PwadResolveFailed(recordingId: String, error: Throwable)

}


