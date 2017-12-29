package com.speedcentral.hm.server.core

import akka.actor.{Actor, ActorLogging, Props}
import com.google.api.services.youtube.YouTube
import com.speedcentral.hm.server.core.UploadManager.CheckConnection
import com.speedcentral.hm.server.youtube.Auth

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UploadManager(
  auth: Auth
) extends Actor with ActorLogging {

  private val youTube: YouTube = buildYouTube()
  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case CheckConnection =>
      val requestor = sender()
      Future {
        checkYouTube()
      }.onComplete {
        case Success(_) =>
          requestor ! "OK"
        case Failure(_) =>
          requestor ! "YouTube connection check failed!"
      }
  }

  private def buildYouTube(): YouTube = {
    val credential = auth.authorize()
    new YouTube.Builder(auth.transport, auth.jsonFactory, credential)
        .setApplicationName("Doom LMPs Uploader")
        .build()
  }

  private def checkYouTube(): Unit = {
    val request = youTube.channels().list("snippet,contentDetails,statistics").setMine(true)
    log.info("Sending request for 'Mine' YouTube channel...")
    val response = request.execute()
    log.info(s"YouTube request result: ${response.toPrettyString}")
  }

}

object UploadManager {
  // Do a basic connection check to verify YT connectivity is working
  case object CheckConnection

  def props(auth: Auth): Props = Props(new UploadManager(auth))
}