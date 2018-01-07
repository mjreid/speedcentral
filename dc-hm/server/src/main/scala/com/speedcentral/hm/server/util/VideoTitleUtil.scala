package com.speedcentral.hm.server.util

import com.speedcentral.hm.server.db.{Pwad, Run}

import scala.util.{Failure, Success, Try}

object VideoTitleUtil {
  def buildVideoTitle(run: Run, primaryPwad: Pwad): String = {
    val iwad = iwadName(run.iwad)
    val category = run.runCategory.map(rc => categoryProperName(rc)).getOrElse("")
    val map = mapProperName(run.iwad, run.map, run.episode)

    val pwadDisplay = if (!isIwad(primaryPwad)) {
      s" ${primaryPwad.fileName} -"
    } else {
      ""
    }

    val runTimeDisplay = run.runTime.map { rt => s" in $rt" }.getOrElse("")
    val runnerDisplay = run.runner.map { rnr => s" by $rnr" }.getOrElse("")

    s"$iwad - $pwadDisplay $map ($category)$runTimeDisplay$runnerDisplay"
  }

  private def iwadName(iwad: String): String = {
    iwad match {
      case "doom" => "DOOM"
      case "doom2" => "DOOM2"
      case _ => ""
    }
  }

  private def isIwad(pwad: Pwad): Boolean = {
    pwad.idgamesUrl == "iwad"
  }

  private def categoryProperName(runCategory: String): String = {
    runCategory match {
      case "uv-max" => "UV Max"
      case "nm100" => "NM 100"
      case "uv-speed" => "UV Speed"
      case "nm-speed" => "NM Speed"
    }
  }

  private def mapProperName(iwad: String, map: String, episode: String): String = {
    val tryMapAsNumeric = Try {
      map.toInt
    }

    tryMapAsNumeric match {
      case Success(mapInt) =>
        if (iwad == "doom") {
          s"E${episode}M$map"
        } else {
          s"MAP%02d".format(mapInt)
        }
      case Failure(_) => map
    }
  }
}
