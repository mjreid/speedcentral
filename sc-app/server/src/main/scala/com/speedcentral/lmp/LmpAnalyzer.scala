package com.speedcentral.lmp

import java.nio.charset.StandardCharsets

import com.speedcentral.api.LmpAnalysisResult

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class LmpAnalyzer(
  pwadAnalyzer: PwadAnalyzer
) {

  private val iwadParameter = "-iwad"
  private val pwadParameter = "-file"
  private val wadSuffix = ".wad"

  def analyze(lmp: Array[Byte])(implicit ec: ExecutionContext): Future[LmpAnalysisResult] = {
    // First check and ensure that the length is within allowed limits
    val maxLength = 10 * 60 // seconds
    val demoLength = lmp.length / LmpConstants.BytesPerTic / LmpConstants.TicsPerSecond
    if (demoLength > maxLength) {
      Future.failed(InvalidLmpException(s"Estimated demo length $demoLength exceeded maximum length $maxLength"))
    }

    val maybeBasicLmpInfo = buildBasicLmpInfo(lmp)
    maybeBasicLmpInfo.map { basicLmpInfo =>
      val endOfDemoIndex = lmp.indexOf(LmpConstants.EndOfDemoMarker)
      if (endOfDemoIndex == -1) throw InvalidLmpException(s"Did not find end of demo marker; corrupt lmp file?")
      val demoFooterBytes = lmp.slice(endOfDemoIndex + 1, lmp.length)
      val demoFooterString = new String(demoFooterBytes, StandardCharsets.US_ASCII).toLowerCase

      val iwad = findIwad(demoFooterString).getOrElse {
        if (basicLmpInfo.episode > 1) "doom" else "doom2"
      }
      val pwads = findPwads(demoFooterString)
      val engine = getEngine(demoFooterString)

      val resolvedPwads = pwads.map { pwad =>
        pwadAnalyzer.resolvePwadPath(pwad, iwad)
      }

      val sequenced = Future.sequence(resolvedPwads)
      sequenced.map { resolvedPwads =>
        val flattened = resolvedPwads.flatten
        val pwads = flattened.toList match {
          case Nil => (None, Seq.empty)
          case head :: Nil => (Some(head), Seq.empty)
          case head :: tail => (Some(head), tail)
        }

        LmpAnalysisResult(
          episode = Some(basicLmpInfo.episode),
          map = Some(basicLmpInfo.map),
          skillLevel = Some(basicLmpInfo.skillLevel),
          engineVersion = engine,
          iwad = iwad,
          primaryPwad = pwads._1,
          secondaryPwads = pwads._2
        )
      }
    }.getOrElse {
      Future.failed(InvalidLmpException(s"Unsupported engine version ${lmp(0)}"))
    }
  }

  private def getEngine(footer: String): Option[String] = {
    if (footer.contains("prboom-plus")) {
      Some("prboom-plus")
    } else {
      Some("doom2")
    }
  }

  case class BasicLmpInfo(skillLevel: Int, episode: Int, map: Int)

  private def buildBasicLmpInfo(lmp: Array[Byte]): Option[BasicLmpInfo] = {
    if (lmp(0) == LmpConstants.EngineVersion.doom_19) {
      val skillLevel = lmp(LmpConstants.Doom19Indexes.skillLevel).toInt
      val episode = lmp(LmpConstants.Doom19Indexes.episode).toInt
      val map = lmp(LmpConstants.Doom19Indexes.map).toInt
      Some(BasicLmpInfo(skillLevel, episode, map))
    } else if (lmp(0) == LmpConstants.EngineVersion.boom) {
      val skillLevel = lmp(LmpConstants.BoomIndexes.skillLevel).toInt
      val episode = lmp(LmpConstants.BoomIndexes.episode).toInt
      val map = lmp(LmpConstants.BoomIndexes.map).toInt
      Some(BasicLmpInfo(skillLevel, episode, map))
    } else {
      None
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
