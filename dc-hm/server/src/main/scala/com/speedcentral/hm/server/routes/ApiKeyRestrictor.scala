package com.speedcentral.hm.server.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{MissingHeaderRejection, Route}
import org.slf4j.LoggerFactory

class ApiKeyRestrictor(
  masterApiKey: String
) {

  private val logger = LoggerFactory.getLogger(classOf[ApiKeyRestrictor])
  private val ApiKeyHeader = "X-HmApiKey"

  def keyed(inner: Route): Route = {
    headerValueByName(ApiKeyHeader) { key =>
      if (key == masterApiKey) {
        inner
      } else {
        logger.warn(s"Received request with invalid API key: $key")
        reject(MissingHeaderRejection(ApiKeyHeader))
      }
    }
  }
}