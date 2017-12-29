package com.speedcentral.hm.server.core

import java.nio.file.{Files, Path}
import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.google.api.client.googleapis.media.MediaHttpUploader.UploadState
import com.google.api.client.googleapis.media.{MediaHttpUploader, MediaHttpUploaderProgressListener}
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.{Video, VideoSnippet, VideoStatus}
import com.speedcentral.hm.server.core.UploadManager.{CheckConnection, UploadVideo}
import com.speedcentral.hm.server.youtube.Auth

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UploadManager(
  auth: Auth,
  uploadExecutionContext: ExecutionContext
) extends Actor with ActorLogging {

  private val youTube: YouTube = buildYouTube()

  override def receive: Receive = {
    case CheckConnection =>
      val requestor = sender()
      Future {
        checkYouTube()
      }(context.dispatcher).onComplete {
        case Success(_) =>
          requestor ! "OK"
        case Failure(_) =>
          requestor ! "YouTube connection check failed!"
      }(context.dispatcher)

    case UploadVideo(runId, videoToUpload) =>
      val notifyActor = sender()
      Future {
        uploadYouTubeVideo(runId, videoToUpload, notifyActor)
      }(uploadExecutionContext).onComplete {
        case Success(info) =>
          notifyActor ! info
        case Failure(e) =>
          log.error(e, "Error occurred uploading YouTube video")
      }(uploadExecutionContext)

  }

  private def buildYouTube(): YouTube = {
    val credential = auth.authorize()
    new YouTube.Builder(auth.transport, auth.jsonFactory, credential)
        .setApplicationName("Doom LMPs Uploader")
        .build()
  }

  private def checkYouTube(): Unit = {
    val request = youTube.channels().list("snippet,contentDetails,statistics").setMine(true)
    log.info("Sending request for 'Mine' YouTube channel...")
    val response = request.execute()
    log.info(s"YouTube request result: ${response.toPrettyString}")
  }

  private def uploadYouTubeVideo(runId: String, videoToUpload: Path, notifyActor: ActorRef): UploadStartedInfo = {
    log.info(s"Building YouTube metadata for $runId")
    val metadata = new Video()
    val videoStatus = new VideoStatus()
    videoStatus.setPrivacyStatus("unlisted")
    metadata.setStatus(videoStatus)

    val snippet = new VideoSnippet()
    snippet.setTitle(s"Doom LMPs Uploader Test - ${LocalDateTime.now().toString}")
    snippet.setDescription(s"Testing LMP uploader (from local dev box) on ${LocalDateTime.now().toString}")
    snippet.setTags(Seq("doom").asJava)

    metadata.setSnippet(snippet)

    val inputStream = Files.newInputStream(videoToUpload)
    try {
      val content = new InputStreamContent("video/*", inputStream)
      val videoInsert = youTube.videos.insert("snippet,statistics,status", metadata, content)
      val uploader = videoInsert.getMediaHttpUploader

      val progressListener = new MediaHttpUploaderProgressListener {
        override def progressChanged(uploader: MediaHttpUploader): Unit = {
          uploader.getUploadState match {
            case UploadState.MEDIA_COMPLETE =>
              val uploadCompletedInfo = UploadCompletedInfo(runId)
              notifyActor ! uploadCompletedInfo
            case state =>
              log.info(s"New upload state in $runId progress listener: $state")
          }
        }
      }

      uploader.setProgressListener(progressListener)

      log.info(s"Uploading $runId video...")
      val returnedVideo = videoInsert.execute()
      log.info(s"Uploaded $runId. YouTube ID: ${returnedVideo.getId}")

      UploadStartedInfo(runId, returnedVideo.getId)

    } finally {
      inputStream.close()
    }
  }

}

object UploadManager {
  // Do a basic connection check to verify YT connectivity is working
  case object CheckConnection

  case class UploadVideo(runId: String, videoToUpload: Path)

  def props(auth: Auth, uploadExecutionContext: ExecutionContext): Props =
    Props(new UploadManager(auth, uploadExecutionContext))
}