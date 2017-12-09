package com.speedcentral.configuration

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, get, onSuccess, parameters, pathPrefix}
import akka.http.scaladsl.server.Route
import com.speedcentral.controllers.SearchController

import scala.concurrent.ExecutionContext


class SearchRouter(
  searchController: SearchController,
  executionContext: ExecutionContext
) extends ScRouteDefinition with SprayJsonSupport {

  implicit private val ec: ExecutionContext = executionContext
  import com.speedcentral.api.JsonFormatters._

  override def buildRoutes(): Route = {
    pathPrefix("search") {
      get {
        parameters('q) { query =>
          onSuccess(searchController.search(query)) { result =>
            complete {
              result
            }
          }
        }
      }
    }
  }
}