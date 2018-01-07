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

  def createRecordingHistory(recordingId: Long, state: String, details: Option[String] = None): Future[Unit] = {
    Future {
      DB localTx { implicit session =>
        val currentTime = Instant.now()
        sql"""insert into recording_history (recording_id, state, history_time, details)
             values (${recordingId}, ${state}, ${currentTime}, ${details})""".update().apply()
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

  def loadPwads(recordingId: Long)(implicit session: DBSession = ReadOnlyAutoSession): Seq[Pwad] = {
    val (pwad, run, rsp, rec) = (Pwad.syntax("pwad"), Run.syntax("run"), RunSecondaryPwads.syntax("rsp"), Recording.syntax("rec"))
    // Load run first
    val maybeRun = withSQL {
      select.from(Run as run).leftJoin(Recording as rec).on(run.id, rec.runId).where.eq(rec.id, recordingId)
    }.map(Run(run)).first().apply()
    val loadedRun = maybeRun.getOrElse(throw HmDbException(s"Couldn't find run ${run.id}"))

    // Load primary PWAD
    val maybePrimaryPwad = withSQL {
      select.from(Pwad as pwad).where.eq(pwad.id, loadedRun.primaryPwadId)
    }.map(Pwad(pwad)).first().apply
    val primaryPwad = maybePrimaryPwad.getOrElse(throw HmDbException(s"Recording $recordingId didn't have valid primary PWAD id"))
    // Load secondary pwads
    val secondaryPwads = withSQL {
      select.from(RunSecondaryPwads as rsp)
            .leftJoin(Pwad as pwad).on(rsp.pwadId, pwad.id)
            .where.eq(rsp.runId, loadedRun.id)
    }.map(Pwad(pwad)).list().apply

    Seq(primaryPwad) ++ secondaryPwads

    // withSQL { select.from(Pwad as pwad).where.in(pwad.fileName, pwadFilenames) }.map(Pwad(p)).list().apply()
  }

  def loadRunOfRecording(recordingId: Long)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[Run]] = {
    Future {
      val (run, rec) = (Run.syntax("run"), Recording.syntax("rec"))
      withSQL {
        select.from(Run as run).leftJoin(Recording as rec).on(run.id, rec.runId).where.eq(rec.id, recordingId)
      }.map(Run(run)).first().apply()
    }
  }
}
