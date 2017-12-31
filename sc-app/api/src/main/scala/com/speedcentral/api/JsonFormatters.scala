package com.speedcentral.api

import spray.json._
import spray.json.DefaultJsonProtocol

object JsonFormatters extends DefaultJsonProtocol {
  implicit val searchResultFormat: RootJsonFormat[SearchResult] = jsonFormat2(SearchResult)
  implicit val searchResultsFormat: RootJsonFormat[SearchResults] = jsonFormat1(SearchResults)
  implicit val feedItemFormat: RootJsonFormat[FeedItem] = jsonFormat4(FeedItem)
  implicit val feedItemsFormat: RootJsonFormat[FeedItems] = jsonFormat1(FeedItems)
  implicit val lmpAnalysisResultFormat: RootJsonFormat[LmpAnalysisResult] = jsonFormat6(LmpAnalysisResult)
  implicit val createRunResultFormat: RootJsonFormat[CreateRunResult] = jsonFormat1(CreateRunResult)
}
