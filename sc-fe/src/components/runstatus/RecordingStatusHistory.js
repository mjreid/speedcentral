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
    run_received: 'Recording queued',
    upload_succeeded: 'Video upload complete',
    exe_recording_started: 'Video recording in progress',
    exe_recording_succeeded: 'Video recording complete',
    upload_started: 'Video upload in progress',
    pwad_resolve_started: 'Resolving PWADs',
    pwad_resolve_succeeded: 'Resolving PWADs succeeded',
    pwad_download_started: 'Downloading PWAD in progress',
    pwad_download_succeeded: 'Downloading PWAD succeeded',
    pwad_download_failed: 'Downloading PWAD failed',


    pwad_resolve_failed: 'Failed to resolve PWADs',
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