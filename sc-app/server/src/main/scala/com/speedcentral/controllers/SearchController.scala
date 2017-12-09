package com.speedcentral.controllers

import com.speedcentral.api.{SearchResult, SearchResults}

import scala.concurrent.{ExecutionContext, Future}

class SearchController {
  def search(searchQuery: String)(implicit ec: ExecutionContext): Future[SearchResults] = {
    Future {
      SearchResults(
        List(
          SearchResult(1, "Super Mario Odyssey")
        )
      )
    }
  }
}
