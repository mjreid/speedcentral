package com.speedcentral.hm.server.db

import java.time.Instant

import scalikejdbc._
import scalikejdbc.config.DBs

import scala.concurrent.{ExecutionContext, Future}

class Repository(
  executionContext: ExecutionContext
) {
  DBs.setupAll()

  private implicit val ec: ExecutionContext = executionContext

  def createRecording(runId: Long): Future[Recording] = {
    Future {
      DB localTx { implicit session =>
        val createdDate = Instant.now()
        val insertedId =
          sql"""insert into recording (run_id, created_date)
               values (${runId}, ${createdDate}) RETURNING id""".updateAndReturnGeneratedKey().apply()
        val r = Recording.syntax("r")
        val maybeRecording = sql"""select ${r.result.*} from ${Recording.as(r)} where ${r.id} = ${insertedId}"""
          .map(Recording(r.resultName)).single().apply()

        val currentTime = Instant.now()
        sql"""insert into recording_history (recording_id, state, history_time)
             values (${insertedId}, 'run_received', ${currentTime})""".update().apply()

        maybeRecording.getOrElse(throw HmDbException(s"Could not find recording with id $insertedId"))
      }
    }
  }

  def createRecordingHistory(recordingId: Long, state: String): Future[Unit] = {
    Future {
      DB localTx { implicit session =>
        val currentTime = Instant.now()
        sql"""insert into recording_history (recording_id, state, history_time)
             values (${recordingId}, ${state}, ${currentTime})""".update().apply()
      }
    }
  }

  def addVideoToRecording(recordingId: Long, videoId: String): Future[Unit] = {
    Future {
      DB localTx { implicit session =>
        sql"""update recording set video_id = ${videoId} where id = ${recordingId}""".update().apply()
      }
    }
  }
}
