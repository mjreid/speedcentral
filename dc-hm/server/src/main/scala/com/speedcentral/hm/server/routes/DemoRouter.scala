package com.speedcentral.hm.server.routes


import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import com.speedcentral.hm.api.DemoRequest
import com.speedcentral.hm.server.controller.DemoController
import com.speedcentral.hm.api.JsonFormatters._

import scala.concurrent.ExecutionContext

class DemoRouter(
  demoController: DemoController,
  executionContext: ExecutionContext
) extends ScRouteDefinition with SprayJsonSupport {

  implicit private val ec: ExecutionContext = executionContext

  override def buildRoutes(): Route = {
    path("demorecording") {
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
