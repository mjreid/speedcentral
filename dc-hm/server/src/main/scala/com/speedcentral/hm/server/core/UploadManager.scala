package com.speedcentral.hm.server.core

import java.nio.file.Path

import akka.actor.{Actor, ActorLogging, Props}
import com.speedcentral.hm.server.core.DemoManager.{UploadFailed, UploadStarted, UploadSucceeded}
import com.speedcentral.hm.server.core.UploadManager.{CheckConnection, UploadVideo}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UploadManager(
  youTubeWrapper: YouTubeWrapper,
  uploadExecutionContext: ExecutionContext
) extends Actor with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {
    case CheckConnection =>
      val requestor = sender()
      Future {
        youTubeWrapper.checkYouTube()
      }.onComplete {
        case Success(_) =>
          requestor ! "OK"
        case Failure(_) =>
          requestor ! "YouTube connection check failed!"
      }

    case UploadVideo(recordingId, videoToUpload) =>
      val notifyActor = sender()
      notifyActor ! UploadStarted(recordingId)
      youTubeWrapper.uploadYouTubeVideo(recordingId, videoToUpload, notifyActor)(uploadExecutionContext).onComplete {
        case Success(videoId) =>
          notifyActor ! UploadSucceeded(recordingId, videoId)
        case Failure(e) =>
          log.error(e, "Error occurred uploading YouTube video")
          notifyActor ! UploadFailed(recordingId, e.getMessage)
      }
  }
}

object UploadManager {
  // Do a basic connection check to verify YT connectivity is working
  case object CheckConnection

  case class UploadVideo(runId: String, videoToUpload: Path)

  def props(youTubeWrapper: YouTubeWrapper, uploadExecutionContext: ExecutionContext): Props =
    Props(new UploadManager(youTubeWrapper, uploadExecutionContext))
}