package com.speedcentral.hm.server.config

import com.typesafe.config.Config

case class IdgamesConfig(
  idgamesBaseUrl: String
)

object IdgamesConfig {
  def fromConfig(config: Config): IdgamesConfig = {
    val baseUrl = config.getString("dc-hm.idgames.base-url")
    IdgamesConfig(baseUrl)
  }
}