package com.speedcentral.hm.server.routes


import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.speedcentral.hm.api.DemoRequest
import com.speedcentral.hm.server.controller.DemoController
import com.speedcentral.hm.api.JsonFormatters._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class DemoRouter(
  demoController: DemoController,
  executionContext: ExecutionContext,
  apiKeyRestrictor: ApiKeyRestrictor
) extends ScRouteDefinition with SprayJsonSupport {

  implicit private val ec: ExecutionContext = executionContext
  implicit val timeout: Timeout = Timeout(10.seconds)

  override def buildRoutes(): Route = {
    path("demorecording") {
      apiKeyRestrictor.keyed {
        post {
          entity(as[DemoRequest]) { demoRequest =>
            onSuccess(demoController.startDemoRequest(demoRequest)) { result =>
              complete {
                result
              }
            }
          }
        }
      }
    }
  }
}
