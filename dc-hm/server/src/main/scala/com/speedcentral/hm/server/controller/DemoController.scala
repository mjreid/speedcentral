package com.speedcentral.hm.server.controller

import com.speedcentral.hm.api.{DemoRequest, DemoResponse}

import scala.concurrent.{ExecutionContext, Future}

class DemoController {

  def startDemoRequest(demoRequest: DemoRequest)(implicit ec: ExecutionContext): Future[DemoResponse] = {
    ???
  }
}
