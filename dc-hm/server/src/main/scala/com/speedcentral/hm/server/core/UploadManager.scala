package com.speedcentral.hm.server.core

import java.nio.file.Path

import akka.actor.{Actor, ActorLogging, Props}
import com.speedcentral.hm.server.core.DemoManager.UploadStarted
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

    case UploadVideo(runId, videoToUpload) =>
      val notifyActor = sender()
      Future {
        youTubeWrapper.uploadYouTubeVideo(runId, videoToUpload, notifyActor)
      }(uploadExecutionContext).onComplete {
        case Success(info) =>
          notifyActor ! UploadStarted(info)
        case Failure(e) =>
          log.error(e, "Error occurred uploading YouTube video")
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