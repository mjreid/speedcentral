package com.speedcentral.hm.server.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.speedcentral.hm.server.core.UploadManager.CheckConnection

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class MetadataRouter(
  uploadManager: ActorRef,
  executionContext: ExecutionContext,
  apiKeyRestrictor: ApiKeyRestrictor
) extends ScRouteDefinition {

  implicit val ec: ExecutionContext = executionContext
  implicit val timeout: Timeout = Timeout(10.seconds)

  override def buildRoutes(): Route = {
    path("version") {
      get {
        complete(
          HttpEntity(
            ContentTypes.`application/json`,
            """{"version":"0.0.1"}"""
          )
        )
      }
    } ~
    path("status") {
      apiKeyRestrictor.keyed {
        get {
          onSuccess((uploadManager ? CheckConnection).mapTo[String]) { result =>
            complete(
              HttpEntity(
                ContentTypes.`application/json`,
                s"""{"result":"$result"}"""
              )
            )
          }
        }
      }
    }
  }
}