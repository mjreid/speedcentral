package com.speedcentral.controllers

import com.speedcentral.ScAppException
import com.speedcentral.api._
import com.speedcentral.db.{ApiConverter, Repository}
import com.speedcentral.hm.HmClient
import com.speedcentral.lmp.{LmpAnalyzer, PwadAnalyzer}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

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
    repository.createRun(createRunRequest).map { createdRun =>
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
      repository.loadRun(runId).map { maybeRunData =>
        maybeRunData.map { case(run, recordings, recordingHistories) =>
          val apiRun = apiConverter.buildRun(run, recordings, recordingHistories)
          RunStatusResponse(apiRun)
        }.getOrElse {
          throw ScAppException(s"Run $runIdStr not found")
        }
      }
    }
  }
}
