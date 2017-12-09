package com.speedcentral.configuration

import akka.http.scaladsl.server.Route

trait ScRouteDefinition {
  def buildRoutes(): Route
}