package com.speedcentral.controllers

import com.speedcentral.api.LmpAnalysisResult
import com.speedcentral.lmp.LmpAnalyzer

import scala.concurrent.{ExecutionContext, Future}

class LmpController {

  val lmpAnalyzer = new LmpAnalyzer

  def analyzeLmp(lmp: Array[Byte])(implicit ec: ExecutionContext): Future[LmpAnalysisResult] = {
    Future {
      lmpAnalyzer.analyze(lmp).get
    }
  }
}
