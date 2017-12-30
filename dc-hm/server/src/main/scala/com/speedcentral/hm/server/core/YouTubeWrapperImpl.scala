package com.speedcentral.hm.server.core

import java.nio.file.{Files, Path}
import java.time.LocalDateTime

import akka.actor.ActorRef
import com.google.api.client.googleapis.media.MediaHttpUploader.UploadState
import com.google.api.client.googleapis.media.{MediaHttpUploader, MediaHttpUploaderProgressListener}
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.{Video, VideoSnippet, VideoStatus}
import com.speedcentral.hm.server.config.YouTubeConfig
import com.speedcentral.hm.server.core.DemoManager.UploadSucceeded
import com.speedcentral.hm.server.youtube.YouTubeAuth
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class YouTubeWrapperImpl(
  youTubeConfig: YouTubeConfig,
  auth: YouTubeAuth
) extends YouTubeWrapper {

  private val log = LoggerFactory.getLogger(classOf[YouTubeWrapperImpl])

  private val youTube = buildYouTube()

  /**
    * Perform a simple read-only check to verify YouTube connectivity.
    */
  def checkYouTube(): Unit = {
    val request = youTube.channels().list("snippet,contentDetails,statistics").setMine(true)
    log.info("Sending request for 'Mine' YouTube channel...")
    val response = request.execute()
    log.info(s"YouTube request result: ${response.toPrettyString}")
  }

  def uploadYouTubeVideo(recordingId: String, videoToUpload: Path, notifyActor: ActorRef): UploadStartedInfo = {
    log.info(s"Building YouTube metadata for $recordingId")
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
              notifyActor ! UploadSucceeded(recordingId)
            case state =>
              log.info(s"New upload state in $recordingId progress listener: $state")
          }
        }
      }

      uploader.setProgressListener(progressListener)

      log.info(s"Uploading $recordingId video...")
      val returnedVideo = videoInsert.execute()
      log.info(s"Uploaded $recordingId. YouTube ID: ${returnedVideo.getId}")

      UploadStartedInfo(recordingId, returnedVideo.getId)

    } finally {
      inputStream.close()
    }
  }

  private def buildYouTube(): YouTube = {
    val credential = auth.authorize()
    new YouTube.Builder(auth.transport, auth.jsonFactory, credential)
      .setApplicationName("Doom LMPs Uploader")
      .build()
  }
}
