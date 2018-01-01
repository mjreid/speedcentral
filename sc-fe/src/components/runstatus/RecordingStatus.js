import React, { Component } from 'react';
import PropTypes from 'prop-types';
import RecordingStatusHistory from "./RecordingStatusHistory";

export default class RecordingStatus extends Component {

  static propTypes = {
    recordings: PropTypes.arrayOf(PropTypes.shape({
      recordingId: PropTypes.string,
      videoId: PropTypes.string,
      history: PropTypes.arrayOf(PropTypes.shape({
        recordingHistoryId: PropTypes.string,
        state: PropTypes.string,
        historyTime: PropTypes.string
      }))
    }))
  };

  // Overall status based on latest status
  static overallStatuses = {
    run_received: 'Queued',
    upload_succeeded: 'Complete',
    exe_recording_started: 'In progress',
    exe_recording_succeeded: 'In progress',
    upload_started: 'In progress',

    exe_recording_failed: 'Failed',
    upload_failed: 'Failed'
  };

  render() {
    const { recordings } = this.props;
    if (recordings.length === 0) {
      return (
        <div />
      );
    } else {
      const recording = recordings[0];
      if (recording.history.length > 0) {
        return (
          <div>
            <div>
              Recording status: {RecordingStatus.overallStatuses[recording.history[0]]}
            </div>
            <div>
              {recording.history.map((history) => <RecordingStatusHistory history={history} />)}
            </div>
          </div>
        );
      } else {
        return (<div />);
      }
    }
  }
}