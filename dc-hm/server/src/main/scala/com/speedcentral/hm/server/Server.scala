package com.speedcentral.hm.server

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.speedcentral.hm.server.controller.DemoController
import com.speedcentral.hm.server.routes.{DemoRouter, MetadataRouter}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Server
  extends RequestLogger {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val bindingFuture = Http().bindAndHandle(buildRoutes(), "localhost", 10666)

    system.registerOnTermination({
      println("Terminating!")
      bindingFuture.flatMap(_.unbind())
    })
  }

  private def buildRoutes(): Route = {
    val executionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))

    val demoController = new DemoController
    val demoRouter = new DemoRouter(demoController, executionContext)
    val metadataRouter = new MetadataRouter

    logResponseTime(demoRouter.buildRoutes() ~ metadataRouter.buildRoutes())
  }

}
