package com.speedcentral.controllers

import com.speedcentral.api.{FeedItem, FeedItems}
import spray.json.{JsObject, JsString}

import scala.concurrent.{ExecutionContext, Future}

class FeedController {
  def defaultNewsFeed()(implicit ec: ExecutionContext): Future[FeedItems] = {
    Future {
      FeedItems(
        List(
          FeedItem(
            itemType = "message",
            id = "1",
            date = "2017-12-14T00:29:40.276Z",
            data = JsObject("header" -> JsString("hello"), "content" -> JsString("Welcome"))
          ),
          FeedItem(
            itemType = "speedrun",
            id = "2",
            date = "2017-12-17T12:20:40.276Z",
            data = JsObject(
              "runner" -> JsString("Leeroy"),
              "game" -> JsString("Super Mario Odyssey"),
              "runTime" -> JsString("PT10M12.345S")
            )
          )
        )
      )
    }
  }
}
