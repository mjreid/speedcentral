package com.speedcentral.hm.server.config

import java.nio.file.{Files, Path, Paths}

import com.speedcentral.hm.server.util.PathUtil
import com.typesafe.config.Config
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

case class HmConfig(
  masterApiKey: String,
  prboomPlusExe: Path,
  iwadDirectory: Path,
  pwadDirectory: Path,
  lmpDirectory: Path,
  vidDirectory: Path,
  youTubeConfig: YouTubeConfig
)

object HmConfig {

  private val logger: Logger = LoggerFactory.getLogger(classOf[HmConfig])

  def fromConfig(config: Config): Try[HmConfig] = {
    Try {
      val masterApiKey = config.getString("dc-hm.master-api-key")
      val prboomPlusExe = config.getString("dc-hm.prboom-plus-exe")
      val iwadDirectory = config.getString("dc-hm.iwad-directory")
      val pwadDirectory = config.getString("dc-hm.pwad-directory")
      val lmpDirectory = config.getString("dc-hm.lmp-directory")
      val vidDirectory = config.getString("dc-hm.vid-directory")

      val prboomPlusExeFile = Paths.get(prboomPlusExe)
      // validate the executable
      if (!Files.exists(prboomPlusExeFile)) {
        throw new HmConfigException(s"$prboomPlusExe did not exist")
      } else if (!Files.isRegularFile(prboomPlusExeFile)) {
        throw new HmConfigException(s"$prboomPlusExe was not a file")
      } else if (!Files.isExecutable(prboomPlusExeFile)) {
        throw new HmConfigException(s"$prboomPlusExe cannot be executed")
      }

      // validate iwads
      val iwadDirectoryFile = Paths.get(iwadDirectory)
      PathUtil.validateDirectory(iwadDirectory, iwadDirectoryFile)

      if (!Files.exists(iwadDirectoryFile.resolve(IWads.Doom2))) {
        logger.warn(s"${IWads.Doom2} was not found in $iwadDirectory")
      }
      if (!Files.exists(iwadDirectoryFile.resolve(IWads.Doom))) {
        logger.warn(s"${IWads.Doom} was not found in $iwadDirectory")
      }

      // validate pwads
      val pwadDirectoryFile = Paths.get(pwadDirectory)
      PathUtil.validateDirectory(pwadDirectory, pwadDirectoryFile)

      // validate lmp directory
      val lmpDirectoryFile = Paths.get(lmpDirectory)
      PathUtil.validateDirectory(lmpDirectory, lmpDirectoryFile, checkWritable = true)

      // validate video directory
      val vidDirectoryFile = Paths.get(vidDirectory)
      PathUtil.validateDirectory(vidDirectory, vidDirectoryFile, checkWritable = true)

      val youTubeConfig = YouTubeConfig.fromConfig(config)

      HmConfig(
        masterApiKey,
        prboomPlusExeFile,
        iwadDirectoryFile,
        pwadDirectoryFile,
        lmpDirectoryFile,
        vidDirectoryFile,
        youTubeConfig
      )
    }
  }
}
