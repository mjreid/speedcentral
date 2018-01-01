package com.speedcentral.db

import java.time.Instant

import scalikejdbc._

case class Run(
  id: Long,
  map: Int,
  episode: Int,
  skillLevel: Int,
  iwad: String,
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
  override val columns = Seq("id", "map", "episode", "skill_level", "iwad", "engine_version",
    "runner", "submitter", "run_category", "run_time", "created_date", "modified_date")

  def apply(r: SyntaxProvider[Run])(rs: WrappedResultSet): Run = apply(r.resultName)(rs)
  def apply(run: ResultName[Run])(rs: WrappedResultSet): Run = {
    new Run(
      rs.long(run.id),
      rs.int(run.map),
      rs.int(run.episode),
      rs.int(run.skillLevel),
      rs.string(run.iwad),
      rs.string(run.engineVersion),
      rs.stringOpt(run.runner),
      rs.stringOpt(run.submitter),
      rs.stringOpt(run.runCategory),
      rs.stringOpt(run.runTime),
      rs.timestamp(run.createdDate).toInstant,
      rs.timestamp(run.modifiedDate).toInstant
    )
  }
}
