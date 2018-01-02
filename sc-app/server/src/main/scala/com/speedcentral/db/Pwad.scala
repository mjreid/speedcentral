package com.speedcentral.db

import scalikejdbc._

case class Pwad(
  id: Long,
  fileName: String,
  idgamesUrl: String,
  friendlyName: Option[String]
)

object Pwad extends SQLSyntaxSupport[Pwad] {
  override val tableName = "pwad"
  override val columns = Seq("id", "file_name", "idgames_url", "friendly_name")

  def apply(p: SyntaxProvider[Pwad])(rs: WrappedResultSet): Pwad = apply(p.resultName)(rs)
  def apply(p: ResultName[Pwad])(rs: WrappedResultSet): Pwad = {
    new Pwad(
      rs.long(p.id),
      rs.string(p.fileName),
      rs.string(p.idgamesUrl),
      rs.stringOpt(p.friendlyName)
    )
  }
}
