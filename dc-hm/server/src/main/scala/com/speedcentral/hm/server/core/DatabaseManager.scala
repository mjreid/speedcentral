package com.speedcentral.hm.server.core

import akka.actor.{Actor, ActorLogging, Props}
import com.speedcentral.hm.server.db.Repository
import com.speedcentral.hm.server.util.DbUtil._

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

    case LogPwadDownloadStarted(recordingId, pwadId, pwadUrl) =>
      extractId(recordingId) { id =>
        val message = Some(s"Download PWAD $pwadId from $pwadUrl")
        repository.createRecordingHistory(id, "pwad_download_started", message)
      }

    case LogPwadDownloadSucceeded(recordingId, pwadId, pwadUrl, details) =>
      extractId(recordingId) { id =>
        val message = Some(s"Download PWAD $pwadId from $pwadUrl SUCCESS: $details")
        repository.createRecordingHistory(id, "pwad_download_succeeded", message)
      }

    case LogPwadDownloadFailed(recordingId, pwadId, pwadUrl, errorDetails) =>
      extractId(recordingId) { id =>
        val message = Some(s"Download PWAD $pwadId from $pwadUrl FAILED: $errorDetails")
        repository.createRecordingHistory(id, "pwad_download_failed", message)
      }

    case LogPwadResolveStarted(recordingId) =>
      extractId(recordingId) { id =>
        repository.createRecordingHistory(id, "pwad_resolve_started")
      }

    case LogPwadResolveSucceeded(recordingId) =>
      extractId(recordingId) { id =>
        repository.createRecordingHistory(id, "pwad_resolve_succeeded")
      }

    case LogPwadResolvedFailed(recordingId, errorDetails) =>
      extractId(recordingId) { id =>
        repository.createRecordingHistory(id, "pwad_resolve_failed", Some(errorDetails))
      }
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

  case class LogPwadDownloadStarted(recordingId: String, pwadId: Long, pwadUrl: String)

  case class LogPwadDownloadSucceeded(recordingId: String, pwadId: Long, pwadUrl: String, details: String)

  case class LogPwadDownloadFailed(recordingId: String, pwadId: Long, pwadUrl: String, errorDetails: String)

  case class LogPwadResolveStarted(recordingId: String)

  case class LogPwadResolveSucceeded(recordingId: String)

  case class LogPwadResolvedFailed(recordingId: String, errorDetails: String)
}

