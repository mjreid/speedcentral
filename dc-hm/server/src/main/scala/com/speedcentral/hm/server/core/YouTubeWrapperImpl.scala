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
import com.speedcentral.hm.server.db.{HmDbException, Pwad, Repository, Run}
import com.speedcentral.hm.server.util.{DbUtil, VideoTitleUtil}
import com.speedcentral.hm.server.youtube.YouTubeAuth
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class YouTubeWrapperImpl(
  youTubeConfig: YouTubeConfig,
  auth: YouTubeAuth,
  repository: Repository
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

  def uploadYouTubeVideo(recordingId: String, videoToUpload: Path, notifyActor: ActorRef)(implicit ec: ExecutionContext): Future[String] = {
    DbUtil.extractIdFlat(recordingId) { id =>
      for {
        maybeRun <- repository.loadRunOfRecording(id)
        run = maybeRun.getOrElse(throw HmDbException(s"Run for $recordingId not found?"))
        pwads = repository.loadPwads(id)
        primaryPwad = pwads.find(p => p.id == run.primaryPwadId).getOrElse(throw HmDbException(s"No primary PWAD for recording $recordingId?"))
      } yield uploadVideoInternal(recordingId, videoToUpload, notifyActor, run, primaryPwad)
    }
  }

  private def uploadVideoInternal(recordingId: String, videoToUpload: Path, notifyActor: ActorRef, run: Run, primaryPwad: Pwad): String = {
    log.info(s"Building YouTube metadata for $recordingId")
    val title = VideoTitleUtil.buildVideoTitle(run, primaryPwad)

    val metadata = new Video()
    val videoStatus = new VideoStatus()
    videoStatus.setPrivacyStatus("public")
    metadata.setStatus(videoStatus)

    val snippet = new VideoSnippet()
    snippet.setTitle(title)
    snippet.setDescription(s"$title. Uploaded by Doom LMP Uploader")
    snippet.setTags(Seq("doom", "speedrun").asJava)

    metadata.setSnippet(snippet)

    val inputStream = Files.newInputStream(videoToUpload)
    try {
      val content = new InputStreamContent("video/*", inputStream)
      val videoInsert = youTube.videos.insert("snippet,statistics,status", metadata, content)
      val uploader = videoInsert.getMediaHttpUploader

      val progressListener = new MediaHttpUploaderProgressListener {
        override def progressChanged(uploader: MediaHttpUploader): Unit = {
          uploader.getUploadState match {
            case state =>
              log.info(s"New upload state in $recordingId progress listener: $state")
          }
        }
      }

      uploader.setProgressListener(progressListener)

      log.info(s"Uploading $recordingId video...")
      val returnedVideo = videoInsert.execute()
      log.info(s"Uploaded $recordingId. YouTube ID: ${returnedVideo.getId}")

      returnedVideo.getId

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
