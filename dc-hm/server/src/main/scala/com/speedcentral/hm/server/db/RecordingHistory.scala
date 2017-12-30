package com.speedcentral.hm.server.db

import java.time.Instant

import scalikejdbc._

case class RecordingHistory(id: Long, recordingId: Long, state: String, historyTime: Instant)

object RecordingHistory extends SQLSyntaxSupport[RecordingHistory] {
  override val tableName = "recording_history"
  override val columns = Seq("id", "recording_id", "state", "history_time")

  def apply(recordingHistory: ResultName[RecordingHistory])(rs: WrappedResultSet) =
    new RecordingHistory(rs.long(recordingHistory.id), rs.long(recordingHistory.recordingId),
      rs.string(recordingHistory.state), rs.timestamp(recordingHistory.historyTime).toInstant)
}