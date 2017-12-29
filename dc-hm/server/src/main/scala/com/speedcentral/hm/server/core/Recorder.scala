package com.speedcentral.hm.server.core

import java.nio.file.{Files, Path, StandardOpenOption}

import com.speedcentral.hm.server.config.{HmConfig, IWads}
import org.slf4j.LoggerFactory

import scala.sys.process._

class Recorder(hmConfig: HmConfig) {

  private val logger = LoggerFactory.getLogger(classOf[Recorder])

  def beginRecording(runId: String, lmpData: Array[Byte]): RecordingResult = {
    val lmpPath = saveLmpDataToFile(runId, lmpData)
    val cmd = buildCommand(runId, lmpPath)
    logger.info(cmd.mkString(" "))

    // This will block while prboom runs!
    cmd.!

    val expectedOutput = buildOutputVideoPath(runId)
    waitForFileToBeWritten(expectedOutput)

    if (!Files.exists(expectedOutput)) {
      logger.info(s"Didn't find expected file ${expectedOutput.toString} after 10s!")
      RecordingFailure(runId, "stdout", "stderr")
    } else {
      logger.info(s"Recording succeeded for ${expectedOutput.toString}")
      RecordingSuccess(runId, "stdout", "stderr", expectedOutput)
    }
  }

  private def waitForFileToBeWritten(path: Path): Unit = {
    // This is horrible and hackish but, just loop for 10 seconds, since there's apparently a delay between prboom
    // exiting and the file being written.
    val retryLimit = 10
    var retryNum = 0
    var done = false

    while (retryNum < retryLimit && !done) {
      if (!Files.exists(path)) {
        retryNum = retryNum + 1
        Thread.sleep(1000)
      } else {
        done = true
      }
    }
  }

  private def saveLmpDataToFile(runId: String, lmpData: Array[Byte]): Path = {
    val targetFile = hmConfig.lmpDirectory.resolve(runId + ".lmp")
    Files.write(targetFile, lmpData, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
  }

  private def buildCommand(runId: String, lmp: Path): Seq[String] = {
    Seq(
      hmConfig.prboomPlusExe.normalize().toString,
      "-iwad",
      hmConfig.iwadDirectory.resolve(IWads.Doom2).normalize().toString,
      "-timedemo",
      lmp.normalize().toString,
      "-viddump",
      buildOutputVideoPath(runId).toString
    )
  }

  private def buildOutputVideoPath(runId: String): Path = {
    hmConfig.vidDirectory.resolve(s"$runId.mkv").normalize()
  }
}
