package com.speedcentral.hm.server.core

import java.nio.file.Path

import akka.actor.ActorRef

trait YouTubeWrapper {
  def checkYouTube(): Unit
  def uploadYouTubeVideo(runId: String, videoToUpload: Path, notifyActor: ActorRef): UploadStartedInfo
}