package com.speedcentral.hm

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

// The classes in this file are copy and pasted from the HM, which isn't optimal but they're so simple it's easier
// than doing any kind of shared library + associated dependency management.

case class DemoMetadata(
  runId: String
)

case class DemoRequest(
  demoMetadata: DemoMetadata,
  lmpData: String // base64 string
)

case class DemoResponse(
  recordingId: String
)

object HmApiJsonFormatters extends DefaultJsonProtocol {
  implicit val demoMetadataFormat: RootJsonFormat[DemoMetadata] = jsonFormat1(DemoMetadata)
  implicit val demoRequestFormat: RootJsonFormat[DemoRequest] = jsonFormat2(DemoRequest)
  implicit val demoResponseFormat: RootJsonFormat[DemoResponse] = jsonFormat1(DemoResponse)
}
