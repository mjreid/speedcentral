package com.speedcentral.db

import java.time.Instant

import scalikejdbc._

case class RecordingHistory(
  id: Long,
  recordingId: Long,
  state: String,
  historyTime: Instant,
  details: Option[String]
)

object RecordingHistory extends SQLSyntaxSupport[RecordingHistory] {
  override val tableName = "recording_history"
  override val columns = Seq("id", "recording_id", "state", "history_time", "details")

  def apply(r: SyntaxProvider[RecordingHistory])(rs: WrappedResultSet): RecordingHistory = apply(r.resultName)(rs)
  def apply(recordingHistory: ResultName[RecordingHistory])(rs: WrappedResultSet): RecordingHistory = {
    new RecordingHistory(
      rs.long(recordingHistory.id),
      rs.long(recordingHistory.recordingId),
      rs.string(recordingHistory.state),
      rs.timestamp(recordingHistory.historyTime).toInstant,
      rs.stringOpt(recordingHistory.details)
    )
  }
}