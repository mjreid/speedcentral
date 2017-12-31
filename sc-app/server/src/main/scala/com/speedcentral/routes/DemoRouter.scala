package com.speedcentral.routes

import com.speedcentral.configuration.ScRouteDefinition
import com.speedcentral.controllers.LmpController
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Sink
import com.speedcentral.api.JsonFormatters._

import scala.concurrent.ExecutionContext

class DemoRouter(
  lmpController: LmpController,
  executionContext: ExecutionContext
) extends ScRouteDefinition with SprayJsonSupport {

  implicit val ec: ExecutionContext = executionContext

  override def buildRoutes(): Route = {
    pathPrefix("demo") {
      path("analyze") {
        extractRequestContext { ctx =>
          implicit val materializer = ctx.materializer

          fileUpload("lmp") {
            case (metadata, fileStream) =>
              val resultF = fileStream
                .runWith(Sink.fold(Array.empty[Byte])((arr, byteString) => arr ++ byteString.toArray[Byte]))
                .flatMap { lmpBytes => lmpController.analyzeLmp(lmpBytes) }

              onSuccess(resultF) { result => complete(result) }
          }
        }
      }
    }
  }
}
