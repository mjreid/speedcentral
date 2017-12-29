package com.speedcentral.hm.server.db

case class HmDbException(message: String) extends RuntimeException(message)
