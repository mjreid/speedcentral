package com.speedcentral.lmp

case class InvalidLmpException(message: String) extends RuntimeException(message)