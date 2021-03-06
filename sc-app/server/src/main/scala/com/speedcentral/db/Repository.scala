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

  def createRun(request: CreateRunRequest, maybeRunTimeMs: Option[Long]): Run = {
    DB localTx { implicit session =>
      // create PWADs first, need them because run references pwads
      val allPwads = request.primaryPwad.toSeq ++ request.secondaryPwads
      val dbPwads = createPwadsIfTheyDoNotExist(allPwads.map(p => Pwad(0, p.pwadFilename, p.pwadIdgamesLocation, Some(""))))(session)

      val primaryPwad = request.primaryPwad.getOrElse(throw ScAppException("Unexpected error: primary PWAD required"))
      val primaryPwadId = dbPwads.find(p => p.fileName == primaryPwad.pwadFilename).map{_.id}.getOrElse(throw ScAppException("Unexpected error: PWAD id not found"))
      val createdDate = Instant.now()
      val createdRunId =
        sql"""insert into run (map, episode, skill_level, iwad, primary_pwad_id, engine_version, runner,
                               submitter, run_category, run_time, created_date, modified_date)
              values (${request.map}, ${request.episode}, ${request.skillLevel}, ${request.iwad}, ${primaryPwadId}, ${request.engineVersion},
                      ${request.runner}, ${request.submitter}, ${request.category}, ${maybeRunTimeMs}, ${createdDate}, ${createdDate})
              returning id
         """.updateAndReturnGeneratedKey().apply()


      // Insert the runId/secondary pwad mappings
      val secondaryPwadBatchParams = dbPwads.filter(p => p.id != primaryPwadId).map(sec => Seq(createdRunId, sec.id))
      sql"insert into run_secondary_pwads (run_id, pwad_id) values (?, ?)".batch(secondaryPwadBatchParams: _*).apply()

      val r = Run.syntax("r")
      val maybeCreatedRun =
        sql"""
          select ${r.result.*} from ${Run.as(r)} where ${r.id} = ${createdRunId}
        """.map(Run(r.resultName)).single().apply()
      maybeCreatedRun.getOrElse(throw ScAppException(s"Could not find $createdRunId!"))
    }
  }

  def loadRun(runId: Long)(implicit session: DBSession = ReadOnlyAutoSession): Option[(Run, Seq[Recording], Seq[RecordingHistory])] = {
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

  def createPwadsIfTheyDoNotExist(pwads: Seq[Pwad])(implicit session: DBSession = AutoSession): Seq[Pwad] = {
    val loadedPwadFilenames = loadPwads(pwads.map(_.fileName))(session).map(_.fileName)
    val pwadsToCreate = pwads.filter(p => !loadedPwadFilenames.contains(p.fileName))
    pwadsToCreate.map(toCreate => createPwad(toCreate.fileName, toCreate.idgamesUrl, toCreate.friendlyName))
    loadPwads(pwads.map(_.fileName))(session)
  }

  def loadPwads(pwadFilenames: Seq[String])(implicit session: DBSession = ReadOnlyAutoSession): Seq[Pwad] = {
    val p = Pwad.syntax("pwad")
    withSQL { select.from(Pwad as p).where.in(p.fileName, pwadFilenames) }.map(Pwad(p)).list().apply()
  }

  def createPwad(filename: String, idgamesUrl: String, friendlyName: Option[String])(implicit session: DBSession = AutoSession): Long = {
    sql"""
        insert into pwad (file_name, idgames_url, friendly_name) values (${filename}, ${idgamesUrl}, ${friendlyName})
      """.updateAndReturnGeneratedKey().apply()
  }
}
