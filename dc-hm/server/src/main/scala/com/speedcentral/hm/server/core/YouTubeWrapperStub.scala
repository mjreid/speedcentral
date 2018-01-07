package com.speedcentral.hm.server.core

import java.nio.file.Path

import akka.actor.ActorRef
import com.speedcentral.hm.server.db.{HmDbException, Repository}
import com.speedcentral.hm.server.util.{DbUtil, VideoTitleUtil}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

class YouTubeWrapperStub(repository: Repository) extends YouTubeWrapper {

  private val logger = LoggerFactory.getLogger(classOf[YouTubeWrapperStub])

  override def checkYouTube(): Unit = {}

  override def uploadYouTubeVideo(recordingId: String, videoToUpload: Path, notifyActor: ActorRef)
    (implicit ec: ExecutionContext): Future[String] = {
    DbUtil.extractIdFlat(recordingId) { id =>
      for {
        maybeRun <- repository.loadRunOfRecording(id)
        run = maybeRun.getOrElse(throw HmDbException(s"Run for $recordingId not found?"))
        pwads = repository.loadPwads(id)
        primaryPwad = pwads.find(p => p.id == run.primaryPwadId).getOrElse(throw HmDbException(s"No primary PWAD for recording $recordingId?"))
      } yield {
        val title = VideoTitleUtil.buildVideoTitle(run, primaryPwad)
        logger.info(s"Not-uploading video $title")
        "NOTAREALVIDEOID"
      }
    }
  }
}
