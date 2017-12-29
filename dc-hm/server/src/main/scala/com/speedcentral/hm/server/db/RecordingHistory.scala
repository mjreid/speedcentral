package com.speedcentral.hm.server.db

import scalikejdbc._

case class RecordingHistory(id: Long, recordingId: Long)

object RecordingHistory extends SQLSyntaxSupport[RecordingHistory] {
  override val tableName = "recording_history"
  override val columns = Seq("id", "recording_id")
}