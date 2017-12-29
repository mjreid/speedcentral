package com.speedcentral.hm.server.core

import java.util.Base64

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.speedcentral.hm.api.DemoRequest
import com.speedcentral.hm.server.core.RecordingManager.BeginRecording

class DemoManager(
  databaseManager: ActorRef,
  recordingManager: ActorRef
) extends Actor with ActorLogging {

  import DemoManager._

  override def receive: Receive = {
    case StartDemo(demoRequest) =>
      log.info(s"Received demo request for ${demoRequest.demoMetadata.runId}")
      val lmpDataBytes = Base64.getDecoder.decode(demoRequest.lmpData)
      recordingManager ! BeginRecording(demoRequest.demoMetadata.runId, lmpDataBytes)
  }
}

object DemoManager {
  def props(databaseManager: ActorRef, recordingManager: ActorRef): Props = Props(new DemoManager(databaseManager, recordingManager))

  case class StartDemo(demoRequest: DemoRequest)

  case class RecordingComplete(recordingResult: RecordingResult)

  case class UploadComplete(uploadResult: Any)
}


