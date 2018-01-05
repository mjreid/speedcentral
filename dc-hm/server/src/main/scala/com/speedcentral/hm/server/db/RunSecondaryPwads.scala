package com.speedcentral.hm.server.db

import scalikejdbc._

case class RunSecondaryPwads(
  id: Long,
  runId: Long,
  pwadId: Long
)

object RunSecondaryPwads extends SQLSyntaxSupport[RunSecondaryPwads] {
  override val tableName = "run_secondary_pwads"
  override val columns = Seq("id", "run_id", "pwad_id")

  def apply(rsp: SyntaxProvider[RunSecondaryPwads])(rs: WrappedResultSet): RunSecondaryPwads = apply(rsp.resultName)(rs)
  def apply(rsp: ResultName[RunSecondaryPwads])(rs: WrappedResultSet): RunSecondaryPwads = {
    new RunSecondaryPwads(
      rs.long(rsp.id),
      rs.long(rsp.runId),
      rs.long(rsp.pwadId)
    )
  }
}