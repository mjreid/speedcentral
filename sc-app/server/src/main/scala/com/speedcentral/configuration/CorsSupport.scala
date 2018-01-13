package com.speedcentral.configuration

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
import com.typesafe.config.ConfigFactory

/**
  * Trait that configures responses to allow CORS support.
  * https://gist.github.com/jeroenr/5261fa041d592f37cd80
  */
trait CorsSupport {
  lazy val allowedOrigin: HttpOrigin = {
    HttpOrigin(ConfigFactory.load().getString("app.cors-address"))
  }

  private def addAccessControlHeaders(): Directive0 = {
    respondWithHeaders(
      `Access-Control-Allow-Origin`(allowedOrigin)
    )
  }

  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK).withHeaders(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
  }

  def corsHandler(r: Route): Route = addAccessControlHeaders() {
    preflightRequestHandler ~ r
  }
}
