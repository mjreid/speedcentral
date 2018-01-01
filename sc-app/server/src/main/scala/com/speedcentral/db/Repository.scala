package com.speedcentral.db

import java.time.{Duration, Instant}
import java.util.concurrent.TimeUnit

import com.speedcentral.ScAppException
import com.speedcentral.api.CreateRunRequest
import scalikejdbc._
import scalikejdbc.config.DBs

import scala.concurrent.{ExecutionContext, Future}

class Repository(
  executionContext: ExecutionContext
) {

  DBs.setupAll()
  implicit val ec: ExecutionContext = executionContext

  def createRun(request: CreateRunRequest): Future[Run] = {
    Future {
      DB localTx { implicit session =>
        val createdDate = Instant.now()
        val createdRunId =
          sql"""insert into run (map, episode, skill_level, iwad, engine_version, runner,
                                 submitter, run_category, run_time, created_date, modified_date)
                values (${request.map}, ${request.episode}, ${request.skillLevel}, ${request.iwad}, ${request.engineVersion},
                        ${request.runner}, ${request.submitter}, ${request.category}, 'PT0M0S', ${createdDate}, ${createdDate})
                returning id
           """.updateAndReturnGeneratedKey().apply()

        val r = Run.syntax("r")
        val maybeCreatedRun =
          sql"""
            select ${r.result.*} from ${Run.as(r)} where ${r.id} = ${createdRunId}
          """.map(Run(r.resultName)).single().apply()
        maybeCreatedRun.getOrElse(throw ScAppException(s"Could not find $createdRunId!"))
      }
    }
  }

  def loadRun(runId: Long)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[(Run, Seq[Recording], Seq[RecordingHistory])]] = {
    Future {
      val (run, rec, rech) = (Run.syntax("run"), Recording.syntax("rec"), RecordingHistory.syntax("rech"))
      val maybeRun: Option[Run] = withSQL { select.from(Run as run).where.eq(run.id, runId) }.map(Run(run)).single().apply()
      maybeRun.map { run =>
        val recordings: List[Recording] = withSQL { select.from(Recording as rec).where.eq(rec.runId, runId) }.map(Recording(rec)).list().apply()
        val recordingIds = recordings.map(r => r.id)
        val recordingHistories: List[RecordingHistory] =
          withSQL { select.from(RecordingHistory as rech).where.in(rech.recordingId, recordingIds) }.map(RecordingHistory(rech)).list().apply()
        (run, recordings, recordingHistories)
      }
    }
  }
}
