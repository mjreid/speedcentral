package com.speedcentral.hm.server.core

import akka.actor.{Actor, ActorLogging, Props}
import com.speedcentral.hm.server.core.DemoManager.RecordingComplete

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class RecordingManager(
  recordingExecutionContext: ExecutionContext,
  recorder: Recorder
) extends Actor with ActorLogging {

  import RecordingManager._
  import context.dispatcher

  override def receive: Receive = {
    case BeginRecording(runId, lmp) =>
      val requestor = sender()
      // Run the beginRecording call on the separate EC.
      Future {
        recorder.beginRecording(runId, lmp)
      }(recordingExecutionContext).onComplete {
        case Success(recordingResult) =>
          requestor ! RecordingComplete(recordingResult)
        case Failure(e) =>
          log.error(e, "Error recording demo")
      }

  }
}

object RecordingManager {

  def props(recordingExecutionContext: ExecutionContext, recorder: Recorder): Props =
    Props(new RecordingManager(recordingExecutionContext, recorder))

  case class BeginRecording(runId: String, lmp: Array[Byte])


}
