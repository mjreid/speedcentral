package com.speedcentral.hm.server.db

import scalikejdbc.config.DBs
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

class Repository(
  executionContext: ExecutionContext
) {
  DBs.setupAll()

  private implicit val ec: ExecutionContext = executionContext

  def createRecording(runId: Long): Future[Recording] = {
    Future {
      DB localTx { implicit session =>
        val insertedId = sql"""insert into recording (run_id) values (${runId}) RETURNING id""".updateAndReturnGeneratedKey().apply()
        val r = Recording.syntax("r")
        val maybeRecording = sql"""select ${r.result.*} from ${Recording.as(r)} where ${r.id} = ${insertedId}"""
          .map(Recording(r.resultName)).single().apply()

        maybeRecording.getOrElse(throw HmDbException(s"Could not find recording with id $insertedId"))
      }
    }
  }
}
