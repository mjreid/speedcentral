package com.speedcentral.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.speedcentral.api.JsonFormatters._
import com.speedcentral.api.{ApiPwad, CreateRunRequest}
import com.speedcentral.configuration.ScRouteDefinition
import com.speedcentral.controllers.LmpController
import org.slf4j.LoggerFactory
import spray.json._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class DemoRouter(
  lmpController: LmpController,
  executionContext: ExecutionContext
) extends ScRouteDefinition with SprayJsonSupport {

  implicit val ec: ExecutionContext = executionContext
  private val logger = LoggerFactory.getLogger(classOf[DemoRouter])
  private val timeoutDuration = 10.seconds

  override def buildRoutes(): Route = {
    pathPrefix("pwad") {
      path("resolve") {
        get {
          parameters('pwadFilename, 'iwad) { (pwadFilename, iwad) =>
            onSuccess(lmpController.resolvePwad(pwadFilename, iwad)) { maybePwad =>
              complete(maybePwad)
            }
          }
        }
      }
    } ~
    toStrictEntity(timeoutDuration) {
      pathPrefix("demo") {
        path("analyze") {
          extractRequestContext { ctx =>
            implicit val materializer: Materializer = ctx.materializer

            fileUpload("lmp") {
              case (metadata, fileStream) =>
                val resultF = fileStream
                  .runWith(Sink.fold(Array.empty[Byte])((arr, byteString) => arr ++ byteString.toArray[Byte]))
                  .flatMap { lmpBytes => lmpController.analyzeLmp(lmpBytes) }

                onSuccess(resultF) { result => complete(result) }
            }
          }
        } ~
        path("submit") {
          formFields("iwad", "map".as[Int], "lmp".as[ByteString], "skillLevel".as[Int],
            "engineVersion", "episode".as[Int], "runner".?, "submitter".?,
            "category".?, "runTime".?, "primaryPwad".?, "secondaryPwads".?) {
            (iwad, map, lmpByteString, skillLevel, engineVersion,
              episode, runner, submitter, category, runTime, primaryPwadStr, secondaryPwadsStr) =>

              val primaryPwad = primaryPwadStr.map(_.parseJson.convertTo[ApiPwad])
              val secondaryPwads = secondaryPwadsStr.map { _.parseJson.convertTo[Seq[ApiPwad]] }.getOrElse(Seq.empty)

              val lmp = lmpByteString.toArray[Byte]
              val createRunRequest = CreateRunRequest(
                map,
                episode,
                skillLevel,
                iwad,
                engineVersion,
                runner,
                submitter,
                category,
                runTime,
                primaryPwad,
                secondaryPwads,
                lmp
              )
              val createdRun = lmpController.createNewRun(createRunRequest)

              complete(createdRun)
          }
        } ~
        path("status") {
          get {
            parameters('runId) { runId =>
              onSuccess(lmpController.getRunStatus(runId)) { runStatusResponse =>
                complete(runStatusResponse)
              }
            }
          }
        }
      }
    }
  }
}
