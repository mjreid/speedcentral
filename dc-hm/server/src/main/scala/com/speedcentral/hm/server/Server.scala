package com.speedcentral.hm.server

import java.util.concurrent.Executors

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.speedcentral.hm.server.config.{HmConfig, YouTubeConfig}
import com.speedcentral.hm.server.controller.DemoController
import com.speedcentral.hm.server.core._
import com.speedcentral.hm.server.db.Repository
import com.speedcentral.hm.server.routes.{ApiKeyRestrictor, DemoRouter, MetadataRouter}
import com.speedcentral.hm.server.youtube.YouTubeAuth
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
    val dbExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))
    val recorder = new Recorder(hmConfig)
    val repository = new Repository(dbExecutionContext)
    val databaseManager = system.actorOf(DatabaseManager.props(repository))
    val recordingManager = system.actorOf(RecordingManager.props(recordingExecutionContext, recorder))


    val youTubeAuth = new YouTubeAuth(hmConfig.youTubeConfig)
    val youTubeWrapper = buildYouTubeWrapper(hmConfig.youTubeConfig, youTubeAuth)
    val uploadExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
    val uploadManager = system.actorOf(UploadManager.props(youTubeWrapper, uploadExecutionContext))


    val demoManager = system.actorOf(DemoManager.props(databaseManager, recordingManager, uploadManager))

    val bindingFuture = Http().bindAndHandle(buildRoutes(demoManager, uploadManager, databaseManager, hmConfig), "localhost", 10666)

    system.registerOnTermination({
      println("Terminating!")
      bindingFuture.flatMap(_.unbind())
    })
  }

  private def buildYouTubeWrapper(youTubeConfig: YouTubeConfig, youTubeAuth: YouTubeAuth): YouTubeWrapper = {
    if (youTubeConfig.enabled) {
      logger.info("YouTube ENABLED.")
      new YouTubeWrapperImpl(youTubeConfig, youTubeAuth)
    } else {
      logger.info("YouTube DISABLED, using stub implementation.")
      new YouTubeWrapperStub
    }
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

  private def buildRoutes(demoManager: ActorRef, uploadManager: ActorRef, databaseManager: ActorRef, hmConfig: HmConfig): Route = {
    val primaryExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))

    val demoController = new DemoController(demoManager, databaseManager)
    val apiKeyRestrictor = new ApiKeyRestrictor(hmConfig.masterApiKey)

    val demoRouter = new DemoRouter(demoController, primaryExecutionContext, apiKeyRestrictor)
    val metadataRouter = new MetadataRouter(uploadManager, primaryExecutionContext, apiKeyRestrictor)

    logResponseTime(demoRouter.buildRoutes() ~ metadataRouter.buildRoutes())
  }

}
