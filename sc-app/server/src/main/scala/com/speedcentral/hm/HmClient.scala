package com.speedcentral.hm

import java.util.Base64

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import scala.concurrent.{ExecutionContext, Future}

class HmClient(
  baseUrl: String,
  apiKey: String
) extends SprayJsonSupport {
  implicit val system: ActorSystem = ActorSystem("hm-client")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val apiKeyHeader = "X-HmApiKey"
  private val demoRecordingPath = "/demorecording"

  import HmApiJsonFormatters._

  def createDemoRecording(runId: String, lmpBytes: Array[Byte])(implicit ec: ExecutionContext): Future[DemoResponse] = {
    val lmpBase64 = Base64.getEncoder.encodeToString(lmpBytes)
    val demoRequest = DemoRequest(DemoMetadata(runId), lmpBase64)
    val uri = baseUrl + demoRecordingPath
    val header = RawHeader(apiKeyHeader, apiKey)

    for {
      request <- Marshal(demoRequest).to[RequestEntity]
      response <-
        Http()
          .singleRequest(HttpRequest(method = HttpMethods.POST, uri = uri, entity = request)
          .withHeaders(header))
      entity <- Unmarshal(response.entity).to[DemoResponse]
    } yield entity
  }

}
