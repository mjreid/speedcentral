package com.speedcentral.hm.api

case class DemoRequest(
  demoMetadata: DemoMetadata,
  lmpData: Array[Byte]
)
