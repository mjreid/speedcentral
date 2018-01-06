package com.speedcentral.hm.server.core

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.speedcentral.hm.server.core.DatabaseManager.{LogPwadDownloadFailed, LogPwadDownloadStarted, LogPwadDownloadSucceeded}
import com.speedcentral.hm.server.core.DemoManager.{PwadResolveFailed, PwadResolveSucceeded}
import com.speedcentral.hm.server.core.PwadDownloader.ResolvePwads
import com.speedcentral.hm.server.db.Repository
import com.speedcentral.hm.server.idgames.{DownloadSucceeded, FileExisted, IdgamesClient, PwadWasIwad}
import com.speedcentral.hm.server.util.DbUtil._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class PwadDownloader(
  idgamesClient: IdgamesClient,
  repository: Repository,
  executionContext: ExecutionContext
) extends Actor with ActorLogging {

  implicit val ec: ExecutionContext = executionContext

  override def receive: Receive = {
    case ResolvePwads(recordingId) =>
      val requestor = sender()
      log.info(s"Resolving PWADs for recordingID $recordingId")

      extractId(recordingId) { recId =>
        val pwads = repository.loadPwads(recId)
        val pwadFutures = pwads.map { p =>
          requestor ! LogPwadDownloadStarted(recordingId, p.id, p.idgamesUrl)
          val downloadF = idgamesClient.resolvePwad(p)

          downloadF.onComplete {
            case Success(PwadWasIwad) =>
              requestor ! LogPwadDownloadSucceeded(recordingId, p.id, p.idgamesUrl, "IWAD-only")
            case Success(FileExisted) =>
              requestor ! LogPwadDownloadSucceeded(recordingId, p.id, p.idgamesUrl, "PWAD already existed")
            case Success(DownloadSucceeded(bytes, time)) =>
              val message = s"Downloaded $bytes in ${time}ms"
              requestor ! LogPwadDownloadSucceeded(recordingId, p.id, p.idgamesUrl, message)
            case Failure(e) =>
              requestor ! LogPwadDownloadFailed(recordingId, p.id, p.idgamesUrl, e.getMessage)
          }

          downloadF
        }
        Future.sequence(pwadFutures).andThen {
          case Success(_) => requestor ! PwadResolveSucceeded(recordingId)
          case Failure(e) => requestor ! PwadResolveFailed(recordingId, e)
        }
      }.onFailure {
        case e: Throwable =>
          log.error(e, "Error occurred while resolving PWADs")
      }
  }

}

object PwadDownloader {

  def props(idgamesClient: IdgamesClient, repository: Repository, executionContext: ExecutionContext): Props =
    Props(new PwadDownloader(idgamesClient, repository, executionContext))

  case class ResolvePwads(recordingId: String)
}