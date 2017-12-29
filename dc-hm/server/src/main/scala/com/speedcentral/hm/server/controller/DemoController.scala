package com.speedcentral.hm.server.controller

import akka.actor.ActorRef
import com.speedcentral.hm.api.{DemoRequest, DemoResponse}
import com.speedcentral.hm.server.core.DemoManager.StartDemo

import scala.concurrent.{ExecutionContext, Future}

class DemoController(
  demoManager: ActorRef
) {

  def startDemoRequest(demoRequest: DemoRequest)(implicit ec: ExecutionContext): Future[DemoResponse] = {
    Future {
      demoManager ! StartDemo(demoRequest)
      DemoResponse(demoRequest.demoMetadata.runId)
    }
  }
}
