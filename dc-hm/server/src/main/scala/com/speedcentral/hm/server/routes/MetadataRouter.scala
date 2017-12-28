package com.speedcentral.hm.server.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class MetadataRouter extends ScRouteDefinition {
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
    }
  }
}
