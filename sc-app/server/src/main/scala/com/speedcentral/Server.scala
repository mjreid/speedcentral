package com.speedcentral

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.speedcentral.configuration.{CorsSupport, FeedRouter, MetadataRouter, SearchRouter}
import com.speedcentral.controllers.{FeedController, LmpController, SearchController}
import com.speedcentral.db.Repository
import com.speedcentral.hm.HmClient
import com.speedcentral.lmp.{LmpAnalyzer, PwadAnalyzer}
import com.speedcentral.routes.DemoRouter
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Server
  extends CorsSupport
  with RequestLogger {

  def main(args: Array[String]): Unit = {
    // Set time zone to UTC so Instant.now() doesn't resolve to PDT or something weird.
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"))

    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val bindingFuture = Http().bindAndHandle(buildRoutes(), "localhost", 8080)

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
      searchController, executionContexts.slowExecutionContext
    ).buildRoutes()
    val feedController = new FeedController()
    val feedRoutes = new FeedRouter(
      feedController, executionContexts.slowExecutionContext
    ).buildRoutes()

    val config = ConfigFactory.load()
    val hmUrl = config.getString("hm.url")
    val hmApiKey = config.getString("hm.api-key")
    val hmClient = new HmClient(hmUrl, hmApiKey)

    val pwadAnalyzer = new PwadAnalyzer(config.getString("idgames.url"))
    val lmpAnalyzer = new LmpAnalyzer(pwadAnalyzer)

    val repository = new Repository(executionContexts.slowExecutionContext)
    val lmpController = new LmpController(repository, hmClient, lmpAnalyzer, pwadAnalyzer)

    val demoRouter = new DemoRouter(lmpController, executionContexts.fastExecutionContext)
    val demoRoutes = demoRouter.buildRoutes()

    corsHandler(logResponseTime(metadataRoutes ~ searchRoutes ~ feedRoutes ~ demoRoutes))
  }

  private def buildExecutionContexts(): ControllerExecutionContexts = {
    ControllerExecutionContexts(
      ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4)),
      ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
    )
  }
}

case class ControllerExecutionContexts(
  fastExecutionContext: ExecutionContext,
  slowExecutionContext: ExecutionContext
)
