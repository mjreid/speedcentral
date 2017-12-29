package com.speedcentral.hm.server.db

import scalikejdbc._

case class Recording(id: Long, runId: Long)

object Recording extends SQLSyntaxSupport[Recording] {
  override val tableName = "recording"
  override val columns = Seq("id", "run_id")

  def apply(recording: ResultName[Recording])(rs: WrappedResultSet) =
    new Recording(rs.long(recording.id), rs.long(recording.runId))
}