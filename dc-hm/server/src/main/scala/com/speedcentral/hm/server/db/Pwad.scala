package com.speedcentral.hm.server.db

import java.nio.file.{Path, Paths}

import scalikejdbc._

case class Pwad(
  id: Long,
  fileName: String,
  idgamesUrl: String,
  friendlyName: Option[String]
) {

  val urlNoExtensionOrPrefix: Option[String] = {
    idgamesUrl match {
      case url if url.length < 5 || !url.endsWith(".zip") => None
      case url => Some(url.substring(0, url.length - 4).substring(1))
    }
  }

  def localPath(pwadDirectory: Path): Path = {
    val urlNoExt = urlNoExtensionOrPrefix.getOrElse(throw new RuntimeException("Couldn't form local path!"))
    pwadDirectory.resolve(urlNoExt).resolve(fileName + ".wad")
  }
}

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
