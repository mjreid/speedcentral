package com.speedcentral.hm.server.core

import akka.actor.{Actor, ActorLogging, Props}
import com.speedcentral.hm.server.db.Repository

import scala.concurrent.Future
import scala.util.{Failure, Success}

class DatabaseManager(
  repository: Repository
) extends Actor with ActorLogging {

  import DatabaseManager._
  import context.dispatcher

  override def receive: Receive = {
    case LogNewRecording(runId) =>
      log.info(s"Logging request for $runId...")
      val requestor = sender()
      Future {
        runId.toLong
      }.map { id =>
        repository.createRecording(id).onComplete {
          case Success(result) =>
            requestor ! NewRecordingLogged(result.id.toString)
          case Failure(e) =>
            log.error(e, "Error while saving recording data.")
        }
      }
  }
}

object DatabaseManager {

  def props(repository: Repository): Props = Props(new DatabaseManager(repository))

  case class LogNewRecording(runId: String)

  case class NewRecordingLogged(recordingId: String)

  case class LogFinishedRecording(recordingId: String)

  case class LogFinishedUploading(recordingId: String)
}

