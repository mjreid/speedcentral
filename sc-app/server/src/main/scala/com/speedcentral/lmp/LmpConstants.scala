package com.speedcentral.lmp

object LmpConstants {
  val EndOfDemoMarker: Byte = 0x80.toByte
  val BytesPerTic: Int = 4
  val TicsPerSecond: Int = 35

  object EngineVersion {
    val doom_19: Byte = 109.toByte
    val boom: Byte = 202.toByte
  }

  object Doom19Indexes {
    val skillLevel = 1
    val episode = 2
    val map = 3
  }

  object BoomIndexes {
    val skillLevel = 8
    val episode = 9
    val map = 10
  }
}
