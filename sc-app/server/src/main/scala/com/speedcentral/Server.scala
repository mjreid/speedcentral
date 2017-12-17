package com.speedcentral

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.speedcentral.configuration.{CorsSupport, MetadataRouter, SearchRouter}
import com.speedcentral.controllers.SearchController

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Server
  extends CorsSupport
  with RequestLogger {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val bindingFuture = Http().bindAndHandle(corsHandler(buildRoutes()), "localhost", 8080)

    system.registerOnTermination({
      println("Terminating!")
      bindingFuture.flatMap(_.unbind())
    })
  }

  private def buildRoutes(): Route = {
    val executionContexts = buildExecutionContexts()

    val metadataRoutes = new MetadataRouter().buildRoutes()
    val searchController = new SearchController()
    val searchRoutes = new SearchRouter(
      searchController, executionContexts.searchExecutionContext
    ).buildRoutes()

    logResponseTime(metadataRoutes ~ searchRoutes)
  }

  private def buildExecutionContexts(): ControllerExecutionContexts = {
    ControllerExecutionContexts(
      ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4)),
      ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
    )
  }
}

case class ControllerExecutionContexts(
  primaryExecutionContext: ExecutionContext,
  searchExecutionContext: ExecutionContext
)
