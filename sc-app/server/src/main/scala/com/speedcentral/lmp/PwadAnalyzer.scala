package com.speedcentral.lmp

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.speedcentral.ScAppException
import com.speedcentral.api.ApiPwad
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class PwadAnalyzer(
  baseUrl: String
) {
  private implicit val system: ActorSystem = ActorSystem("idgames-viewer")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private val logger = LoggerFactory.getLogger(classOf[PwadAnalyzer])
  import system.dispatcher

  private val doomLevelsBase = "/levels/doom"
  private val doom2LevelsBase = "/levels/doom2"
  private val portsSubdirectory = "/Ports"

  // Special WADs that live in a nonstandard location but are pretty popular for speedrunning or mapmaking.
  private val specialWads = Map(
    "mm2" -> "/themes/mm/mm2.zip",
    "mm2mus" -> "/themes/mm/mm2.zip",
    "mm" -> "/themes/mm/mm_allup.zip",
    "mmmus" -> "/themes/mm/mm_allup.zip",
    "hr" -> "/themes/hr/hr.zip",
    "hr2" -> "/themes/hr/hr2final.zip",
    "icarus" -> "/themes/TeamTNT/icarus/icarus.zip",
    "eternal" -> "/themes/TeamTNT/eternal/eternal.zip",
    "gothictx" -> "/graphics/gothictx.zip"
  )

  // A subset of megaWADs that live in /doom2/megawads; prefixed at runtime to avoid duplication
  private val doom2MegawadPrefix = "/levels/doom2/megawads"
  private val doom2Megawads = Map(
    "av" -> "av.zip",
    "btsx_e1" -> "btsx_e1.zip",
    "cydreams" -> "cydreams.zip",
    "d2twid" -> "d2twid.zip",
    "darken" -> "darken.zip",
    "darken2" -> "darken2.zip",
    "dmonfear" -> "dmonfear.zip",
    "epic2" -> "epic2.zip",
    "intercep" -> "intercep.zip",
    "ksutra" -> "ksutra.zip",
    "marsw301" -> "marsw301.zip",
    "pl2" -> "pl2.zip",
    "rebirth" -> "rebirth.zip",
    "requiem" -> "requiem.zip",
    "reverie" -> "reverie.zip",
    "scythe" -> "scythe.zip",
    "tntr.zip" -> "tntr.zip",
    "tvr" -> "tvr.zip"
  )

  // Same as above, but in the /Ports/megawads directory
  private val doom2PortMegawadPrefix = "/levels/doom2/Ports/megawads"
  private val doom2PortMegawads = Map(
    "1024" -> "1024.zip",
    "1024cla2" -> "1024cla2.zip",
    "10sector" -> "10sector.zip",
    "10secto2" -> "10secto2.zip",
    "aaliens" -> "aaliens.zip",
    "cchest" -> "cchest.zip",
    "cchest2" -> "cchest2.zip",
    "cchest3" -> "cchest3.zip",
    "cchest4" -> "cchest4.zip",
    "gd" -> "gd.zip",
    "ngmvmt2" -> "ngmvmt2.zip",
    "prcp" -> "prcp.zip",
    "resurge" -> "resurge.zip",
    "scythe2" -> "scythe2.zip",
    "sodfinal" -> "sodfinal.zip",
    "sunlust" -> "sunlust.zip",
    "valiant" -> "valiant.zip"
  )

  def resolvePwadPath(pwadNameUnsanitized: String, iwad: String): Future[Option[ApiPwad]] = {
    val pwadName = pwadNameUnsanitized.toLowerCase
    // If the pwad already contains a '/' character we'll assume it's a full path already.
    if (pwadName.contains("/")) {
      Future.successful(Some(ApiPwad(pwadName, pwadName)))
    } else {
      if (specialWads.contains(pwadName)) {
        logger.info(s"Found $pwadName in special wads")
        Future.successful(Some(ApiPwad(pwadName, specialWads(pwadName))))
      } else if (doom2Megawads.contains(pwadName)) {
        logger.info(s"Found $pwadName in doom2 megawads")
        Future.successful(Some(ApiPwad(pwadName, s"$doom2MegawadPrefix/${doom2Megawads(pwadName)}")))
      } else if (doom2PortMegawads.contains(pwadName)) {
        logger.info(s"Found $pwadName in doom2 port megawads")
        Future.successful(Some(ApiPwad(pwadName, s"$doom2PortMegawadPrefix/${doom2PortMegawads(pwadName)}")))
      } else {

        val basePath = if (iwad == "doom") {
          doomLevelsBase
        } else {
          doom2LevelsBase
        }

        val bucket = pwadToBucket(pwadName)

        // test main first
        val idgamesPath = s"$basePath$bucket/$pwadName.zip"
        logger.info(s"Attempting to resolve $idgamesPath...")
        checkPwadExistence(idgamesPath).flatMap { exists =>
          if (exists) {
            logger.info(s"$idgamesPath existed!")
            Future.successful(Some(ApiPwad(pwadName, idgamesPath)))
          } else {
            // test Ports folder
            val portsIdgamesPath = s"$basePath$portsSubdirectory$bucket/$pwadName.zip"
            logger.info(s"$idgamesPath failed, attempting to resolve $portsIdgamesPath...")
            checkPwadExistence(portsIdgamesPath).flatMap { exists =>
              if (exists) {
                logger.info(s"$portsIdgamesPath existed!")
                Future.successful(Some(ApiPwad(pwadName, portsIdgamesPath)))
              } else {
                // Test megawads... this is a damn mess
                val megawadsIdgamesPath = s"$doom2MegawadPrefix/$pwadName.zip"
                logger.info(s"$portsIdgamesPath failed, attempting to resolve $megawadsIdgamesPath...")
                checkPwadExistence(megawadsIdgamesPath).flatMap { exists =>
                  if (exists) {
                    logger.info(s"$megawadsIdgamesPath existed!")
                    Future.successful(Some(ApiPwad(pwadName, megawadsIdgamesPath)))
                  } else {
                    // Test ports megawads. WE HAVE TO GO DEEPER (this is TERRIBLE code)
                    val megawadsPortsIdgamesPath = s"$doom2PortMegawadPrefix/$pwadName.zip"
                    logger.info(s"$portsIdgamesPath failed, attempting to resolve $megawadsPortsIdgamesPath...")
                    checkPwadExistence(megawadsPortsIdgamesPath).map { exists =>
                      if (exists) {
                        logger.info(s"$megawadsPortsIdgamesPath existed!")
                        Some(ApiPwad(pwadName, megawadsPortsIdgamesPath))
                      } else {
                        logger.warn(s"Did not find requested PWAD $pwadName in any resolution path.")
                        None
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private def checkPwadExistence(idgamesPath: String): Future[Boolean] = {
    val uri = Uri(baseUrl + idgamesPath)
    val request = HttpRequest(method = HttpMethods.HEAD, uri = uri)
    Http().singleRequest(request).map { response =>
      response.status == StatusCodes.OK
    }
  }

  private val buckets: Set[(Char, Char)] = Set(
    ('0', '9'),
    ('a', 'c'),
    ('d', 'f'),
    ('g', 'i'),
    ('j', 'l'),
    ('m', 'o'),
    ('p', 'r'),
    ('s', 'u'),
    ('v', 'z')
  )

  // Maps a PWAD to its idgames bucket path based on first character of the WAD
  private def pwadToBucket(pwadName: String): String = {
    if (pwadName.length == 0) throw ScAppException(s"Invalid PWAD name $pwadName")
    val first = pwadName.toLowerCase()(0)
    val maybeBucket = buckets.find(bucket => bucket._1 <= first && bucket._2 >= first)
    maybeBucket.map { case (c1, c2) => s"/$c1-$c2" }
      .getOrElse(throw ScAppException(s"Could not find bucket for $pwadName ($first)"))
  }

  /*def downloadPwad(uri: Uri, downloadTo: Path): Future[Long] = {
    val request = HttpRequest(uri = uri)
    Http().singleRequest(request).flatMap { response =>
      val source = response.entity.dataBytes
      source.runWith(FileIO.toPath(downloadTo))
    }.map { result =>
      result.count
    }
  }*/
}