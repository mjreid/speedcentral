package com.speedcentral.hm.server.util

import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

object DbUtil {
  private val log = LoggerFactory.getLogger(DbUtil.getClass)
  /**
    * Helper method that converts string IDs to long IDs, and logs when an ID is not numeric.
    *
    * Philosophically this is done to encapsulate ID's numeric-ness at the DB level. But it's kind of a pain in the ass
    * to deal with.
    */
  def extractId[T](stringId: String)(inner: Long => T)(implicit ec: ExecutionContext): Future[T] = {
    val resultF = Future {
      stringId.toLong
    }.map(inner)

    resultF.onFailure {
      case e: NumberFormatException =>
        log.error(s"Invalid conversion: id $stringId was not a long.", e)
    }

    resultF
  }
}
