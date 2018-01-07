package com.speedcentral.hm.server.core

import java.nio.file.Path

import akka.actor.ActorRef

class YouTubeWrapperStub extends YouTubeWrapper {
  override def checkYouTube(): Unit = {}

  override def uploadYouTubeVideo(recordingId: String, videoToUpload: Path, notifyActor: ActorRef): String = {
    Thread.sleep(5000)
    "NOTAREALVIDEOID"
  }
}
