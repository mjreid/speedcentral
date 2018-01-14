package com.speedcentral.hm.server.core

import java.nio.file.{Files, Path, StandardOpenOption}

import com.speedcentral.hm.server.config.{HmConfig, IWads}
import com.speedcentral.hm.server.db.{Repository, Run}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._

class Recorder(
  hmConfig: HmConfig,
  repository: Repository
) {

  private val logger = LoggerFactory.getLogger(classOf[Recorder])

  def beginRecording(recordingId: String)(implicit ec: ExecutionContext): Future[RecordingResult] = {
    val lmpPath = buildLmpPath(recordingId)
    if (!Files.exists(lmpPath) || !Files.isRegularFile(lmpPath)) {
      logger.warn(s"LMP not found! ${lmpPath.toString}")
      Future.successful(RecordingFailure(recordingId, "stdout", "stderr"))
    } else {
      val pwads = repository.loadPwads(recordingId.toLong)
      val pwadsToInclude = pwads.filter(_.idgamesUrl.toLowerCase != "iwad").map { p =>
        p.localPath(hmConfig.pwadDirectory)
      }
      repository.loadRunOfRecording(recordingId.toLong).map { maybeRun =>
        val run = maybeRun.getOrElse(throw new RuntimeException("Run not found?"))
        val cmd = buildCommand(recordingId, lmpPath, pwadsToInclude, run)
        logger.info(cmd.mkString(" "))

        val stdout = new StringBuilder
        val stderr = new StringBuilder
        // This will block while prboom runs!
        cmd.! // ProcessLogger(stdout append _, stderr append _)
        logger.info(s"STDOUT for ${cmd.mkString(" ")} - ${stdout.toString()}")
        logger.info(s"STDERR for ${cmd.mkString(" ")} - ${stderr.toString()}")

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

  private def buildCommand(recordingId: String, lmp: Path, pwadsToInclude: Seq[Path], run: Run): Seq[String] = {
    val baseCommand = Seq(
      hmConfig.prboomPlusExe.normalize().toString,
      "-iwad",
      hmConfig.iwadDirectory.resolve(IWads.fromString(run.iwad)).normalize().toString,
      "-timedemo",
      lmp.normalize().toString,
      "-viddump",
      buildOutputVideoPath(recordingId).toString
    )

    val pwads = pwadsToInclude.flatMap (p => makePwadCommand(p))

    baseCommand ++ pwads
  }

  // Hardcoded exceptions for nonstandard, popular WADs.
  private def makePwadCommand(pwadPath: Path): Seq[String] = {
    if (pwadPath.endsWith("btsx_e1.wad")) {
      Seq("-file", pwadPath.getParent.resolve("btsx_e1a.wad").toString,
                   pwadPath.getParent.resolve("btsx_e1b.wad").toString,
          "-deh", pwadPath.getParent.resolve("btsx_e1.deh").toString)
    } else {
      Seq("-file", pwadPath.toString)
    }
  }

  private def buildOutputVideoPath(recordingId: String): Path = {
    hmConfig.vidDirectory.resolve(s"$recordingId.mkv").normalize()
  }
}
