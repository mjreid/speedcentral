package com.speedcentral.hm.server.config

import java.io.File

import com.typesafe.config.Config
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

case class HmConfig(
  masterApiKey: String,
  prboomPlusExe: File,
  iwadDirectory: File,
  pwadDirectory: File
) {

}

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

      val prboomPlusExeFile = new File(prboomPlusExe)
      // validate the executable
      if (!prboomPlusExeFile.exists()) {
        throw new HmConfigException(s"${prboomPlusExe} did not exist")
      } else if (!prboomPlusExeFile.isFile) {
        throw new HmConfigException(s"${prboomPlusExe} was not a file")
      } else if (!prboomPlusExeFile.canExecute) {
        throw new HmConfigException(s"${prboomPlusExe} cannot be executed")
      }

      // validate iwads
      val iwadDirectoryFile = new File(iwadDirectory)
      if (!iwadDirectoryFile.exists()) {
        throw new HmConfigException(s"${iwadDirectory} did not exist")
      } else if (!iwadDirectoryFile.canRead) {
        throw new HmConfigException(s"${iwadDirectory} can't be read")
      } else if (!iwadDirectoryFile.isDirectory) {
        throw new HmConfigException(s"${iwadDirectory} was not a directory")
      }

      if (!new File(iwadDirectoryFile, Doom2).exists()) {
        logger.warn(s"$Doom2 was not found in $iwadDirectory")
      }
      if (!new File(iwadDirectoryFile, Doom).exists()) {
        logger.warn(s"$Doom was not found in $iwadDirectory")
      }

      // validate pwads
      val pwadDirectoryFile = new File(pwadDirectory)
      if (!pwadDirectoryFile.exists()) {
        throw new HmConfigException(s"${pwadDirectory} did not exist")
      } else if (!pwadDirectoryFile.canRead) {
        throw new HmConfigException(s"${pwadDirectory} can't be read")
      } else if (!pwadDirectoryFile.isDirectory) {
        throw new HmConfigException(s"${pwadDirectory} was not a directory")
      }

      HmConfig(masterApiKey, prboomPlusExeFile, iwadDirectoryFile, pwadDirectoryFile)

    }
  }
}
