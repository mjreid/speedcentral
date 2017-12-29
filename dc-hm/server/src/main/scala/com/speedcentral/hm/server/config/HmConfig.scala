package com.speedcentral.hm.server.config

import java.nio.file.{Files, Path, Paths}

import com.typesafe.config.Config
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

case class HmConfig(
  masterApiKey: String,
  prboomPlusExe: Path,
  iwadDirectory: Path,
  pwadDirectory: Path,
  lmpDirectory: Path,
  vidDirectory: Path
)

class HmConfigException(message: String) extends RuntimeException(message)

object HmConfig {

  private val logger: Logger = LoggerFactory.getLogger(classOf[HmConfig])

  val Doom2 = "doom2.wad"
  val Doom = "doom.wad"

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
        throw new HmConfigException(s"${prboomPlusExe} did not exist")
      } else if (!Files.isRegularFile(prboomPlusExeFile)) {
        throw new HmConfigException(s"${prboomPlusExe} was not a file")
      } else if (!Files.isExecutable(prboomPlusExeFile)) {
        throw new HmConfigException(s"${prboomPlusExe} cannot be executed")
      }

      // validate iwads
      val iwadDirectoryFile = Paths.get(iwadDirectory)
      if (!Files.exists(iwadDirectoryFile)) {
        throw new HmConfigException(s"${iwadDirectory} did not exist")
      } else if (!Files.isReadable(iwadDirectoryFile)) {
        throw new HmConfigException(s"${iwadDirectory} can't be read")
      } else if (!Files.isDirectory(iwadDirectoryFile)) {
        throw new HmConfigException(s"${iwadDirectory} was not a directory")
      }

      if (!Files.exists(iwadDirectoryFile.resolve(Doom2))) {
        logger.warn(s"$Doom2 was not found in $iwadDirectory")
      }
      if (!Files.exists(iwadDirectoryFile.resolve(Doom))) {
        logger.warn(s"$Doom was not found in $iwadDirectory")
      }

      // validate pwads
      val pwadDirectoryFile = Paths.get(pwadDirectory)
      if (!Files.exists(pwadDirectoryFile)) {
        throw new HmConfigException(s"${pwadDirectory} did not exist")
      } else if (!Files.isReadable(pwadDirectoryFile)) {
        throw new HmConfigException(s"${pwadDirectory} can't be read")
      } else if (!Files.isDirectory(pwadDirectoryFile)) {
        throw new HmConfigException(s"${pwadDirectory} was not a directory")
      }

      // validate lmp directory
      val lmpDirectoryFile = Paths.get(lmpDirectory)
      if (!Files.exists(lmpDirectoryFile)) {
        throw new HmConfigException(s"${lmpDirectory} did not exist")
      } else if (!Files.isReadable(lmpDirectoryFile)) {
        throw new HmConfigException(s"${lmpDirectory} cannot be read")
      } else if (!Files.isDirectory(lmpDirectoryFile)) {
        throw new HmConfigException(s"${lmpDirectory} was not a directory")
      } else if (!Files.isWritable(lmpDirectoryFile)) {
        throw new HmConfigException(s"${lmpDirectory} was not writable")
      }


      // validate video directory
      val vidDirectoryFile = Paths.get(vidDirectory)
      if (!Files.exists(vidDirectoryFile)) {
        throw new HmConfigException(s"${vidDirectory} did not exist")
      } else if (!Files.isReadable(vidDirectoryFile)) {
        throw new HmConfigException(s"${vidDirectory} cannot be read")
      } else if (!Files.isDirectory(vidDirectoryFile)) {
        throw new HmConfigException(s"${vidDirectory} was not a directory")
      } else if (!Files.isWritable(vidDirectoryFile)) {
        throw new HmConfigException(s"${vidDirectory} was not writable")
      }

      HmConfig(masterApiKey, prboomPlusExeFile, iwadDirectoryFile, pwadDirectoryFile, lmpDirectoryFile, vidDirectoryFile)
    }
  }
}
