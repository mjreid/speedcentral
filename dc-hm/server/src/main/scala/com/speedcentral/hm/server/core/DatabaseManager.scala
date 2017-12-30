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
    case LogRecordingCreated(runId) =>
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

    case LogExeRecordingStarted(recordingId) =>
      extractId(recordingId) { id =>
        repository.createRecordingHistory(id, "exe_recording_started")
      }

    case LogExeRecordingSucceeded(recordingId) =>
      extractId(recordingId) { id =>
        repository.createRecordingHistory(id, "exe_recording_succeeded")
      }

    case LogExeRecordingFailed(recordingId) =>
      extractId(recordingId) { id =>
        repository.createRecordingHistory(id, "exe_recording_failed")
      }

    case LogUploadStarted(recordingId, videoId) =>
      extractId(recordingId) { id =>
        repository.createRecordingHistory(id, "upload_started")
        repository.addVideoToRecording(id, videoId)
      }

    case LogUploadSucceeded(recordingId) =>
      extractId(recordingId) { id =>
        repository.createRecordingHistory(id, "upload_succeeded")
      }

    case LogUploadFailed(recordingId, _) =>
      extractId(recordingId) { id =>
        repository.createRecordingHistory(id, "upload_failed")
      }
  }

  /**
    * Helper method that converts string IDs to long IDs, and logs when an ID is not numeric.
    *
    * Philosophically this is done to encapsulate ID's numeric-ness at the DB level. But it's kind of a pain in the ass
    * to deal with.
    */
  private def extractId[T](stringId: String)(inner: Long => T): Future[T] = {
    val resultF = Future {
      stringId.toLong
    }.map(inner)

    resultF.onFailure {
      case e: NumberFormatException =>
        log.error(e, s"Invalid conversion: id $stringId was not a long.")
    }

    resultF
  }
}

object DatabaseManager {

  def props(repository: Repository): Props = Props(new DatabaseManager(repository))

  case class LogRecordingCreated(runId: String)

  case class NewRecordingLogged(recordingId: String)

  case class LogExeRecordingStarted(recordingId: String)

  case class LogExeRecordingFailed(recordingId: String)

  case class LogExeRecordingSucceeded(recordingId: String)

  case class LogUploadStarted(recordingId: String, videoId: String)

  case class LogUploadSucceeded(recordingId: String)

  case class LogUploadFailed(recordingId: String, error: String)

  case class LogRecordingFailed(recordingId: String)
}

