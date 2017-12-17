package com.speedcentral

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.server.{Directive0, RouteResult}
import akka.http.scaladsl.server.RouteResult.{Complete, Rejected}
import akka.http.scaladsl.server.directives.{DebuggingDirectives, LogEntry, LoggingMagnet}

trait RequestLogger {

  def responseTimeLoggingFunction(adapter: LoggingAdapter, requestTimestamp: Long)(req: HttpRequest)(res: RouteResult): Unit = {
    val entry = res match {
      case Complete(resp) =>
        val responseTimestamp: Long = System.nanoTime()
        val elapsedTime: Long = (responseTimestamp - requestTimestamp) / 1000000
        val loggingMessage = s"""${req.method.value}:${truncateUri(req.uri)}:${resp.status}:${elapsedTime}ms"""
        LogEntry(loggingMessage, Logging.InfoLevel)
      case Rejected(reason) =>
        LogEntry(s"Rejected ${truncateUri(req.uri)}: ${reason.mkString(",")}", Logging.WarningLevel)
    }
    entry.logTo(adapter)
  }

  private def truncateUri(uri: Uri): String = {
    uri.path.toString()
  }

  def printResponseTime(log: LoggingAdapter): HttpRequest => RouteResult => Unit = {
    var requestTimestamp = System.nanoTime()
    responseTimeLoggingFunction(log, requestTimestamp)(_)
  }

  def logResponseTime: Directive0 = DebuggingDirectives.logRequestResult(LoggingMagnet(printResponseTime))
}
