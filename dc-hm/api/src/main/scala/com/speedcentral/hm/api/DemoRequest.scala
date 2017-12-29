package com.speedcentral.hm.api

case class DemoRequest(
  demoMetadata: DemoMetadata,
  lmpData: String // base64 string
)
