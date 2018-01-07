package com.speedcentral.hm.server.db

import java.time.Instant

import scalikejdbc._

case class Run(
  id: Long,
  map: String,
  episode: String,
  skillLevel: String,
  iwad: String,
  primaryPwadId: Long,
  engineVersion: String,
  runner: Option[String],
  submitter: Option[String],
  runCategory: Option[String],
  runTime: Option[String],
  createdDate: Instant,
  modifiedDate: Instant
)

object Run extends SQLSyntaxSupport[Run] {
  override val tableName = "run"
  override val columns = Seq("id", "map", "episode", "skill_level", "iwad", "primary_pwad_id", "engine_version",
    "runner", "submitter", "run_category", "run_time", "created_date", "modified_date")

  def apply(r: SyntaxProvider[Run])(rs: WrappedResultSet): Run = apply(r.resultName)(rs)
  def apply(run: ResultName[Run])(rs: WrappedResultSet): Run = {
    val runTimeMs = rs.longOpt(run.runTime)
    val runTime = runTimeMs.map { ms =>
      val secondsTotal = ms / 1000
      val hours = secondsTotal / 3600
      val minutes = (secondsTotal % 3600) / 60
      val seconds = secondsTotal % 60
      if (hours == 0) {
        "%02d:%02d".format(minutes, seconds)
      } else {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
      }
    }
    new Run(
      rs.long(run.id),
      rs.string(run.map),
      rs.string(run.episode),
      rs.string(run.skillLevel),
      rs.string(run.iwad),
      rs.long(run.primaryPwadId),
      rs.string(run.engineVersion),
      rs.stringOpt(run.runner),
      rs.stringOpt(run.submitter),
      rs.stringOpt(run.runCategory),
      runTime,
      rs.timestamp(run.createdDate).toInstant,
      rs.timestamp(run.modifiedDate).toInstant
    )
  }
}
