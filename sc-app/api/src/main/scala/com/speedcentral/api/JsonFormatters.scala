package com.speedcentral.api

import spray.json._
import spray.json.DefaultJsonProtocol

object JsonFormatters extends DefaultJsonProtocol {
  implicit val searchResultFormat: RootJsonFormat[SearchResult] = jsonFormat1(SearchResult)
  implicit val searchResultsFormat: RootJsonFormat[SearchResults] = jsonFormat1(SearchResults)
}
