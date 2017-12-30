package com.speedcentral.hm.server.db

import java.time.Instant

import scalikejdbc._

case class Recording(id: Long, runId: Long, videoId: String, createdDate: Instant)

object Recording extends SQLSyntaxSupport[Recording] {
  override val tableName = "recording"
  override val columns = Seq("id", "run_id", "video_id", "created_date")

  def apply(recording: ResultName[Recording])(rs: WrappedResultSet) =
    new Recording(rs.long(recording.id), rs.long(recording.runId),
      rs.string(recording.videoId), rs.timestamp(recording.createdDate).toInstant)
}