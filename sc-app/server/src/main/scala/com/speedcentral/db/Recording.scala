package com.speedcentral.db

import java.time.Instant

import scalikejdbc._

case class Recording(
  id: Long,
  runId: Long,
  videoId: Option[String],
  createdDate: Instant
)

object Recording extends SQLSyntaxSupport[Recording] {
  override val tableName = "recording"
  override val columns = Seq("id", "run_id", "video_id", "created_date")

  def apply(r: SyntaxProvider[Recording])(rs: WrappedResultSet): Recording = apply(r.resultName)(rs)
  def apply(recording: ResultName[Recording])(rs: WrappedResultSet): Recording = {
    new Recording(
      rs.long(recording.id),
      rs.long(recording.runId),
      rs.stringOpt(recording.videoId),
      rs.timestamp(recording.createdDate).toInstant
    )
  }
}