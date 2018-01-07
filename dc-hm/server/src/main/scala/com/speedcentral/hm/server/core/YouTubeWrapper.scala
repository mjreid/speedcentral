package com.speedcentral.hm.server.core

import java.nio.file.Path

import akka.actor.ActorRef

import scala.concurrent.{ExecutionContext, Future}

trait YouTubeWrapper {
  def checkYouTube(): Unit
  def uploadYouTubeVideo(recordingId: String, videoToUpload: Path, notifyActor: ActorRef)(implicit ec: ExecutionContext): Future[String]
}