package com.speedcentral.hm.server.config

/**
  * IWAD constants that will never change, i.e. "doom2.wad".
  */
object IWads {
  val Doom2 = "doom2.wad"
  val Doom = "doom.wad"

  def fromString(s: String): String = {
    s match {
      case "doom" => IWads.Doom
      case "doom2" => IWads.Doom2
      case _ => throw new RuntimeException(s"Unknown IWAD $s")
    }
  }
}
