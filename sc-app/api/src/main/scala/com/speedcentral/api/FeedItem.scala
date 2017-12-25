package com.speedcentral.api

import spray.json.JsValue

case class FeedItem(
  itemType: String,
  id: String,
  date: String,
  data: JsValue
)