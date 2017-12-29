package com.speedcentral.hm.server.db

import scalikejdbc._

case class Run(id: Long, submitter: String)

object Run extends SQLSyntaxSupport[Run] {
  override val tableName = "run"
  override val columns = Seq("id", "submitter")

  def apply(run: ResultName[Run])(rs: WrappedResultSet) =
    new Run(rs.long(run.id), rs.string(run.submitter))
}
