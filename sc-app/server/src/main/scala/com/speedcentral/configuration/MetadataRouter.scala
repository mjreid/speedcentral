package com.speedcentral.configuration

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.speedcentral.controllers.SearchController

import scala.concurrent.ExecutionContext



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
