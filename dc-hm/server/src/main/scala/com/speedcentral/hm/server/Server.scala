package com.speedcentral.hm.server

import java.util.concurrent.Executors

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.speedcentral.hm.server.config.HmConfig
import com.speedcentral.hm.server.controller.DemoController
import com.speedcentral.hm.server.core.{DatabaseManager, DemoManager, Recorder, RecordingManager}
import com.speedcentral.hm.server.routes.{DemoRouter, MetadataRouter}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}

object Server
  extends RequestLogger {

  private val logger = LoggerFactory.getLogger(Server.getClass)

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val hmConfig = loadConfig()
    val recordingExecutionContext = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
    val recorder = new Recorder(hmConfig)
    val databaseManager = system.actorOf(DatabaseManager.props())
    val recordingManager = system.actorOf(RecordingManager.props(recordingExecutionContext, recorder))
    val demoManager = system.actorOf(DemoManager.props(databaseManager, recordingManager))

    val bindingFuture = Http().bindAndHandle(buildRoutes(demoManager), "localhost", 10666)

    system.registerOnTermination({
      println("Terminating!")
      bindingFuture.flatMap(_.unbind())
    })
  }

  private def loadConfig(): HmConfig = {
    val config = ConfigFactory.load()
    HmConfig.fromConfig(config) match {
      case Failure(e) =>
        logger.error("Error while validating config, terminating!", e)
        sys.exit(1)
      case Success(hmConfig) => hmConfig
    }
  }

  private def buildRoutes(demoManager: ActorRef): Route = {
    val primaryExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))

    val demoController = new DemoController(demoManager)
    val demoRouter = new DemoRouter(demoController, primaryExecutionContext)
    val metadataRouter = new MetadataRouter

    logResponseTime(demoRouter.buildRoutes() ~ metadataRouter.buildRoutes())
  }

}
