package com.speedcentral.hm.server.core

import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import com.speedcentral.hm.server.config.HmConfig
import org.slf4j.LoggerFactory

class Recorder(hmConfig: HmConfig) {

  private val logger = LoggerFactory.getLogger(classOf[Recorder])

  def beginRecording(runId: String, lmpData: Array[Byte]): RecordingResult = {
    val lmpPath = saveLmpDataToFile(runId, lmpData)
    logger.info(buildCommand(runId, lmpPath))
    RecordingSuccess(runId, "stdout", "stderr")
  }

  private def saveLmpDataToFile(runId: String, lmpData: Array[Byte]): Path = {
    val targetFile = hmConfig.lmpDirectory.resolve(runId + ".lmp")
    Files.write(targetFile, lmpData, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
  }

  private def buildCommand(runId: String, lmp: Path): String = {
    s"${hmConfig.prboomPlusExe.normalize()} -iwad ${hmConfig.iwadDirectory.normalize()}/doom2.wad " +
      s"-timedemo ${lmp.normalize()} -viddump ${hmConfig.vidDirectory.resolve(runId + ".mkv").normalize()}"
  }
}
