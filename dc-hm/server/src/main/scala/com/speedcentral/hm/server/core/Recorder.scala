package com.speedcentral.hm.server.core

import java.nio.file.{Files, Path, StandardOpenOption}

import com.speedcentral.hm.server.config.{HmConfig, IWads}
import com.speedcentral.hm.server.db.Repository
import org.slf4j.LoggerFactory

import scala.sys.process._

class Recorder(
  hmConfig: HmConfig,
  repository: Repository
) {

  private val logger = LoggerFactory.getLogger(classOf[Recorder])

  def beginRecording(recordingId: String): RecordingResult = {
    val lmpPath = buildLmpPath(recordingId)
    if (!Files.exists(lmpPath) || !Files.isRegularFile(lmpPath)) {
      logger.warn(s"LMP not found! ${lmpPath.toString}")
      RecordingFailure(recordingId, "stdout", "stderr")
    } else {
      val pwads = repository.loadPwads(recordingId.toLong)
      val pwadsToInclude = pwads.map { p =>
        p.localPath(hmConfig.pwadDirectory)
      }

      val cmd = buildCommand(recordingId, lmpPath, pwadsToInclude)
      logger.info(cmd.mkString(" "))

      // This will block while prboom runs!
      cmd.!

      val expectedOutput = buildOutputVideoPath(recordingId)
      waitForOutputFileToBeWritten(expectedOutput)

      if (!Files.exists(expectedOutput)) {
        logger.info(s"Didn't find expected file ${expectedOutput.toString} after 10s!")
        RecordingFailure(recordingId, "stdout", "stderr")
      } else {
        logger.info(s"Recording succeeded for ${expectedOutput.toString}")
        RecordingSuccess(recordingId, "stdout", "stderr", expectedOutput)
      }
    }
  }

  private def waitForOutputFileToBeWritten(path: Path): Unit = {
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

  def saveLmpDataToFile(recordingId: String, lmpData: Array[Byte]): Path = {
    val targetFile = buildLmpPath(recordingId)
    Files.write(targetFile, lmpData, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
  }

  private def buildLmpPath(recordingId: String): Path = {
    hmConfig.lmpDirectory.resolve(recordingId + ".lmp")
  }

  private def buildCommand(runId: String, lmp: Path, pwadsToInclude: Seq[Path]): Seq[String] = {
    val baseCommand = Seq(
      hmConfig.prboomPlusExe.normalize().toString,
      "-iwad",
      hmConfig.iwadDirectory.resolve(IWads.Doom2).normalize().toString,
      "-timedemo",
      lmp.normalize().toString,
      "-viddump",
      buildOutputVideoPath(runId).toString
    )

    val pwads = pwadsToInclude.flatMap (pwad => Seq("-file", pwad.toString))

    baseCommand ++ pwads
  }

  private def buildOutputVideoPath(recordingId: String): Path = {
    hmConfig.vidDirectory.resolve(s"$recordingId.mkv").normalize()
  }
}
