import React, { Component } from 'react';
import PropTypes from 'prop-types';
import moment from "moment";

export default class RecordingStatusHistory extends Component {

  static propTypes = {
    history: PropTypes.shape({
      recordingHistoryId: PropTypes.string,
      state: PropTypes.string,
      historyTime: PropTypes.string
    })
  };

  static specificStatuses = {
    run_received: 'Video recording queued',
    upload_succeeded: 'Video upload complete',
    exe_recording_started: 'Recording in progress',
    exe_recording_succeeded: 'Recording complete',
    upload_started: 'Video upload in progress',

    exe_recording_failed: 'Failed to record demo',
    upload_failed: 'Failed to upload video'
  };

  render() {
    const { state, historyTime } = this.props.history;
    const displayStatus = RecordingStatusHistory.specificStatuses[state];
    const displayTime = moment(historyTime).format("LL LTS");
    return (
      <div>{displayTime} - {displayStatus}</div>
    )
  }
}