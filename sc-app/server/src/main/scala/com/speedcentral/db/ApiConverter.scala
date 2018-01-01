package com.speedcentral.db

import com.speedcentral.api.{ApiRecording, ApiRecordingHistory, ApiRun}

class ApiConverter {
  def buildRun(dbRun: Run, dbRecordings: Seq[Recording], dbRecordingHistories: Seq[RecordingHistory]): ApiRun = {

    val apiRecordings = dbRecordings.map { dbr =>
      val histories = dbRecordingHistories
        .filter(dbrh => dbrh.recordingId == dbr.id)
        .sortBy(dbrh => dbrh.historyTime).reverse
        .map { dbrh =>
          ApiRecordingHistory(
            dbrh.id.toString,
            dbrh.state,
            dbrh.historyTime.toString,
            dbrh.details
          )
      }

      ApiRecording(
        dbr.id.toString,
        dbr.videoId,
        histories
      )
    }

    ApiRun(
      dbRun.id.toString,
      dbRun.map,
      dbRun.episode,
      dbRun.skillLevel,
      dbRun.iwad,
      dbRun.engineVersion,
      dbRun.runner,
      dbRun.submitter,
      dbRun.runCategory,
      dbRun.runTime,
      dbRun.createdDate.toString,
      dbRun.modifiedDate.toString,
      apiRecordings
    )
  }
}
