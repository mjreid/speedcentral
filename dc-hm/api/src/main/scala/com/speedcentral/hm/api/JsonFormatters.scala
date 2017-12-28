package com.speedcentral.hm.api

import spray.json._
import spray.json.DefaultJsonProtocol

object JsonFormatters extends DefaultJsonProtocol {
  implicit val demoMetadataFormat: RootJsonFormat[DemoMetadata] = jsonFormat1(DemoMetadata)
  implicit val demoRequestFormat: RootJsonFormat[DemoRequest] = jsonFormat2(DemoRequest)
  implicit val demoResponseFormat: RootJsonFormat[DemoResponse] = jsonFormat1(DemoResponse)
}
