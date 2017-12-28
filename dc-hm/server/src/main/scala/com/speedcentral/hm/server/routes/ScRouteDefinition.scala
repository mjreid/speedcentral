package com.speedcentral.hm.server.routes

import akka.http.scaladsl.server.Route

trait ScRouteDefinition {
  def buildRoutes(): Route
}
