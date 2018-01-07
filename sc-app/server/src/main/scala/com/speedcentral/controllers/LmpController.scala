package com.speedcentral.controllers

import java.time.{Duration, LocalTime}
import java.time.format.DateTimeFormatter

import com.speedcentral.ScAppException
import com.speedcentral.api._
import com.speedcentral.db.{ApiConverter, Repository}
import com.speedcentral.hm.HmClient
import com.speedcentral.lmp.{LmpAnalyzer, PwadAnalyzer}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class LmpController(
  repository: Repository,
  hmClient: HmClient,
  lmpAnalyzer: LmpAnalyzer,
  pwadAnalyzer: PwadAnalyzer
) {

  val apiConverter = new ApiConverter

  def analyzeLmp(lmp: Array[Byte])(implicit ec: ExecutionContext): Future[LmpAnalysisResult] = {
    lmpAnalyzer.analyze(lmp)
  }

  def resolvePwad(pwadFilename: String, iwad: String)(implicit executionContext: ExecutionContext): Future[Option[ApiPwad]] = {
    pwadAnalyzer.resolvePwadPath(pwadFilename, iwad)
  }

  def createNewRun(createRunRequest: CreateRunRequest)(implicit ec: ExecutionContext): Future[CreateRunResult] = {
    val validated = validateRunRequest(createRunRequest)
    Future {
      val maybeRunTimeMs = getRunTimeMs(createRunRequest)
      repository.createRun(validated, maybeRunTimeMs)
    }.map { createdRun =>
      CreateRunResult(runId = createdRun.id.toString)
    }.andThen {
      case Success(result) =>
        hmClient.createDemoRecording(result.runId, createRunRequest.lmpBytes)
    }
  }

  def getRunStatus(runIdStr: String)(implicit ec: ExecutionContext): Future[RunStatusResponse] = {
    Future {
      runIdStr.toLong
    }.flatMap { runId =>
      Future {
        repository.loadRun(runId)
      }.map { maybeRunData =>
        maybeRunData.map { case(run, recordings, recordingHistories) =>
          val apiRun = apiConverter.buildRun(run, recordings, recordingHistories)
          RunStatusResponse(apiRun)
        }.getOrElse {
          throw ScAppException(s"Run $runIdStr not found")
        }
      }
    }
  }

  // Do very basic, frameworkless validation on the run request.
  private def validateRunRequest(request: CreateRunRequest): CreateRunRequest = {
    // If pwad is None (or has an empty name), turn it into the iwad
    val pwad = request.primaryPwad match {
      case p @ Some(ApiPwad(name, _)) if name.trim.nonEmpty => p
      case _ => Some(ApiPwad(request.iwad, "N/A"))
    }

    // Include only secondary pwads that have a nonempty filename
    val secondaryPwads = request.secondaryPwads.filter(p => p.pwadFilename.trim.nonEmpty)

    // If runTime is an invalid string, turn it into None
    val runTime = request.runTime.flatMap { r =>
      Try {
        LocalTime.parse(r, DateTimeFormatter.ofPattern("HH:mm:ss"))
        r
      }.toOption
    }

    request.copy(
      primaryPwad = pwad,
      secondaryPwads = secondaryPwads,
      runTime = runTime
    )
  }

  private def getRunTimeMs(request: CreateRunRequest): Option[Long] = {
    request.runTime.map { r =>
      val localTime = LocalTime.parse(r, DateTimeFormatter.ofPattern("HH:mm:ss"))
      val duration = Duration.between(LocalTime.MIN, localTime)
      duration.toMillis
    }
  }
}
