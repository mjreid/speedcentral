package com.speedcentral.hm.server.controller

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.speedcentral.hm.api.{DemoRequest, DemoResponse}
import com.speedcentral.hm.server.core.DatabaseManager.{LogRecordingCreated, NewRecordingLogged}
import com.speedcentral.hm.server.core.DemoManager.StartDemo

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class DemoController(
  demoManager: ActorRef,
  databaseManager: ActorRef
) {

  implicit val timeout: Timeout = Timeout(10.seconds)

  def startDemoRequest(demoRequest: DemoRequest)(implicit ec: ExecutionContext): Future[DemoResponse] = {
    (databaseManager ? LogRecordingCreated(demoRequest.demoMetadata.runId)).mapTo[NewRecordingLogged].map { newRecordingLogged =>
      demoManager ! StartDemo(newRecordingLogged.recordingId, demoRequest.lmpData)
      DemoResponse(newRecordingLogged.recordingId)
    }
  }
}
