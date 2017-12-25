package com.speedcentral.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import com.speedcentral.controllers.FeedController
import com.speedcentral.api.JsonFormatters._

import scala.concurrent.ExecutionContext

class FeedRouter(
  feedController: FeedController,
  executionContext: ExecutionContext
) extends ScRouteDefinition with SprayJsonSupport {

  implicit private val ec: ExecutionContext = executionContext

  override def buildRoutes(): Route = {
    path("feed") {
      get {
        onSuccess(feedController.defaultNewsFeed()) { result =>
          complete {
            result
          }
        }
      }
    }
  }

}
