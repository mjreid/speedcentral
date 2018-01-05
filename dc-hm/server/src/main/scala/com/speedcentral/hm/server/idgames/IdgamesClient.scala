package com.speedcentral.hm.server.idgames

import java.nio.file.{Files, Path, Paths}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import com.speedcentral.hm.server.config.HmConfig
import com.speedcentral.hm.server.db.Pwad
import org.slf4j.{Logger, LoggerFactory}
import sys.process._

import scala.concurrent.{ExecutionContext, Future}

class IdgamesClient(
  hmConfig: HmConfig,
  executionContext: ExecutionContext
) {
  private implicit val system: ActorSystem = ActorSystem("idgames-client")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = executionContext
  private val logger: Logger = LoggerFactory.getLogger(classOf[IdgamesClient])


  def resolvePwad(pwad: Pwad): Future[PwadResolveResult] = {
    logger.info(s"Resolving pwad $pwad...")
    val destination = buildDestinationPath(pwad).resolve(pwad.fileName)

    if (Files.exists(destination)) {
      logger.info(s"File ${destination.toString} already existed")
      Future.successful(FileExisted)
    } else {
      val uri = Uri(s"${hmConfig.idgamesConfig.idgamesBaseUrl}${pwad.idgamesUrl}")

      logger.info(s"Downloading from ${uri.toString()} to ${destination.toString}")
      val startTime = System.nanoTime()
      downloadPwad(uri, destination).map { result =>
        val unzipPath = destination
        unzipWad(unzipPath)
        val downloadTime = (System.nanoTime() - startTime) / 1000000d
        logger.info(s"Download of ${pwad.fileName} from ${uri.toString()} to ${destination.toString} succeeded in ${downloadTime}ms")
        DownloadSucceeded(result, downloadTime)
      }
    }
  }

  // Build a destination path based on the idgames URL
  private def buildDestinationPath(pwad: Pwad): Path = {
    // This makes paths unique by stripping out the .zip extension from the idgamesUrl and placing it in the base path.
    val basePath = hmConfig.pwadDirectory
    val urlNoExtension = pwad.urlNoExtensionOrPrefix.getOrElse(throw new RuntimeException(s"Invalid looking idgames URL: ${pwad.idgamesUrl}"))
    val pwadPath = basePath.resolve(urlNoExtension)
    if (!Files.exists(pwadPath) || !Files.isDirectory(pwadPath)) {
      Files.createDirectories(pwadPath)
    }

    pwadPath
  }

  private def unzipWad(zipPath: Path): Unit = {
    if (!Files.exists(zipPath) || !Files.isRegularFile(zipPath)) throw new RuntimeException(s"$zipPath not found!")
    val unzipOutput = Process(s"unzip -LL $zipPath", zipPath.getParent.toFile).!!
    logger.info(s"Unzip output: $unzipOutput")
  }

  private def downloadPwad(uri: Uri, destination: Path): Future[Long] = {
    val request = HttpRequest(uri = uri)
    Http().singleRequest(request).flatMap { response =>
      val source = response.entity.dataBytes
      source.runWith(FileIO.toPath(destination))
    }.map(_.count)
  }
}

sealed trait PwadResolveResult
case object FileExisted extends PwadResolveResult
case class DownloadSucceeded(bytes: Long, timeMs: Double) extends PwadResolveResult