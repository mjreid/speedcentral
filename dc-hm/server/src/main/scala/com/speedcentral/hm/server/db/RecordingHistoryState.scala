package com.speedcentral.hm.server.db

import scalikejdbc._

case class RecordingHistoryState(id: Long, state: String, description: String)

object RecordingHistoryState extends SQLSyntaxSupport[RecordingHistory] {
  override val tableName = "recording_history_state"
  override val columns = Seq("id", "state", "description")
}