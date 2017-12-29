package com.speedcentral.hm.server.core

import akka.actor.{Actor, ActorLogging, Props}

class DatabaseManager extends Actor with ActorLogging {

  import DatabaseManager._

  override def receive: Receive = {
    case LogNewRequest(runId) =>
      log.info(s"Logging request for $runId...")
  }
}

object DatabaseManager {

  def props(): Props = Props(new DatabaseManager)

  case class LogNewRequest(runId: String)

  case class LogFinishedRecording(runId: String)

  case class LogFinishedUploading(runId: String)
}

