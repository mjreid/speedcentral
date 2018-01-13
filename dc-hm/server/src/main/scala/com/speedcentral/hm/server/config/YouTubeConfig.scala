package com.speedcentral.hm.server.config

import java.nio.file.{Path, Paths}

import com.speedcentral.hm.server.util.PathUtil
import com.typesafe.config.Config

case class YouTubeConfig(
  credentialsFile: Path,
  credentialsDirectory: Path,
  receiverPort: Int,
  callbackUrl: String,
  enabled: Boolean
)

object YouTubeConfig {

  def fromConfig(config: Config): YouTubeConfig = {
    val credentialsFile = config.getString("dc-hm.youtube.credentials-file")
    val credentialsDirectory = config.getString("dc-hm.youtube.credentials-directory")
    val receiverPort = config.getInt("dc-hm.youtube.receiver-port")
    val callbackUrl = config.getString("dc-hm.youtube.callback-url")
    val enabled = config.getBoolean("dc-hm.youtube.enabled")

    val credentialsDirectoryPath = Paths.get(credentialsDirectory)
    PathUtil.validateDirectory(credentialsDirectory, credentialsDirectoryPath, checkWritable = true)

    val credentialsFilePath = Paths.get(credentialsFile)
    PathUtil.validateFile(credentialsFile, credentialsFilePath)

    YouTubeConfig(credentialsFilePath, credentialsDirectoryPath, receiverPort, callbackUrl, enabled)
  }
}
