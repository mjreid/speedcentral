package com.speedcentral.lmp

import java.nio.charset.StandardCharsets

import com.speedcentral.api.LmpAnalysisResult

import scala.collection.mutable
import scala.util.Try

class LmpAnalyzer(

) {

  private val iwadParameter = "-iwad"
  private val pwadParameter = "-file"
  private val wadSuffix = ".wad"

  def analyze(lmp: Array[Byte]): Try[LmpAnalysisResult] = {
    Try {
      if (lmp(0) == LmpConstants.EngineVersion.doom_19) {
        val skillLevel = lmp(1).toInt
        val episode = lmp(2).toInt
        val map = lmp(3).toInt

        val endOfDemoIndex = lmp.indexOf(LmpConstants.EndOfDemoMarker)
        if (endOfDemoIndex == -1) throw InvalidLmpException(s"Did not find end of demo marker; corrupt lmp file?")
        val demoFooterBytes = lmp.slice(endOfDemoIndex + 1, lmp.length)
        val demoFooterString = new String(demoFooterBytes, StandardCharsets.US_ASCII).toLowerCase

        val iwad = findIwad(demoFooterString)
        val pwads = findPwads(demoFooterString)
        val engine = getEngine(demoFooterString)

        LmpAnalysisResult(
          skillLevel = Some(skillLevel),
          map = Some(map),
          episode = Some(episode),
          engineVersion = eng,
          iwad = iwad,
          pwads = pwads
        )
      } else {
        throw InvalidLmpException(s"Unsupported engine version ${lmp(0)}")
      }
    }
  }

  private def getEngine(footer: String): Option[String] = {
    if (footer.contains("prboom-plus")) {
      Some("prboom-plus")
    } else {
      Some("doom2")
    }
  }

  private def findIwad(footer: String): Option[String] = {
    val iwadParameterIndex = footer.indexOf(iwadParameter)
    if (iwadParameterIndex != -1) {
      val wadEndIndex = footer.substring(iwadParameterIndex + iwadParameter.length).indexOf(wadSuffix)
      if (wadEndIndex != -1) {
        Some(footer.substring(iwadParameterIndex + iwadParameter.length,
          iwadParameterIndex + iwadParameter.length + wadEndIndex).filter(c => c != '"').trim)
      } else {
        None
      }
    } else {
      None
    }
  }

  private def findPwads(footer: String): Seq[String] = {
    var startIndex = footer.indexOf(pwadParameter)
    val wads = new mutable.ListBuffer[String]

    while (startIndex != -1) {
      val wadEndIndex = footer.substring(startIndex + pwadParameter.length).indexOf(wadSuffix)
      if (wadEndIndex != -1) {
        // Make sure there's no '-' character between startIndex and wadEndIndex, or we might be looking at a different
        // parameter's wad
        val substring = footer.substring(startIndex + pwadParameter.length, startIndex + pwadParameter.length + wadEndIndex)
        if (substring.contains("-")) {
          startIndex = -1
        } else {
          val wad = substring.filter(c => c != '"').trim
          wads.append(wad)
          startIndex = startIndex + wadEndIndex + wadSuffix.length
        }
      } else {
        startIndex = -1
      }
    }

    wads.toList
  }
}
