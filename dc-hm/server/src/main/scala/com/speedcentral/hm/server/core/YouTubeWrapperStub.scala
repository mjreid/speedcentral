package com.speedcentral.hm.server.core

import java.nio.file.Path

import akka.actor.ActorRef

class YouTubeWrapperStub extends YouTubeWrapper {
  override def checkYouTube(): Unit = {}

  override def uploadYouTubeVideo(runId: String, videoToUpload: Path, notifyActor: ActorRef): UploadStartedInfo = {
    UploadStartedInfo(runId, "NOTAREALVIDEOID")
  }
}
