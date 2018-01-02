package com.speedcentral.api

import spray.json._
import spray.json.DefaultJsonProtocol

object JsonFormatters extends DefaultJsonProtocol {
  implicit val searchResultFormat: RootJsonFormat[SearchResult] = jsonFormat2(SearchResult)
  implicit val searchResultsFormat: RootJsonFormat[SearchResults] = jsonFormat1(SearchResults)
  implicit val feedItemFormat: RootJsonFormat[FeedItem] = jsonFormat4(FeedItem)
  implicit val feedItemsFormat: RootJsonFormat[FeedItems] = jsonFormat1(FeedItems)
  implicit val pwadFormat: RootJsonFormat[Pwad] = jsonFormat2(Pwad)
  implicit val lmpAnalysisResultFormat: RootJsonFormat[LmpAnalysisResult] = jsonFormat7(LmpAnalysisResult)
  implicit val createRunResultFormat: RootJsonFormat[CreateRunResult] = jsonFormat1(CreateRunResult)

  implicit val recordingHistoryResponseFormat: RootJsonFormat[ApiRecordingHistory] = jsonFormat4(ApiRecordingHistory)
  implicit val recordingResponseFormat: RootJsonFormat[ApiRecording] = jsonFormat3(ApiRecording)
  implicit val runResponseFormat: RootJsonFormat[ApiRun] = jsonFormat13(ApiRun)
  implicit val runStatusResponseFormat: RootJsonFormat[RunStatusResponse] = jsonFormat1(RunStatusResponse)

}
