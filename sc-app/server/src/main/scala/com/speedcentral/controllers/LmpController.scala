package com.speedcentral.controllers

import com.speedcentral.api.{CreateRunRequest, CreateRunResult, LmpAnalysisResult}
import com.speedcentral.db.Repository
import com.speedcentral.hm.HmClient
import com.speedcentral.lmp.LmpAnalyzer

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class LmpController(
  repository: Repository,
  hmClient: HmClient
) {

  val lmpAnalyzer = new LmpAnalyzer

  def analyzeLmp(lmp: Array[Byte])(implicit ec: ExecutionContext): Future[LmpAnalysisResult] = {
    Future {
      lmpAnalyzer.analyze(lmp).get
    }
  }

  def createNewRun(createRunRequest: CreateRunRequest)(implicit ec: ExecutionContext): Future[CreateRunResult] = {
    repository.createRun(createRunRequest).map { createdRun =>
      CreateRunResult(runId = createdRun.id.toString)
    }.andThen {
      case Success(result) =>
        hmClient.createDemoRecording(result.runId, createRunRequest.lmpBytes)
    }
  }
}
