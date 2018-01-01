import React, { Component } from 'react';
import PropTypes from 'prop-types';
import RunVideo from "./RunVideo";
import RunTitle from "./RunTitle";
import RunDetails from "./RunDetails";
import RecordingStatus from "./RecordingStatus";

export default class RunWrapper extends Component {

  static propTypes = {
    run: PropTypes.shape({
      map: PropTypes.number,
      runCategory: PropTypes.string,
      runTime: PropTypes.string,
      modifiedDate: PropTypes.string,
      runner: PropTypes.string,
      skillLevel: PropTypes.number,
      iwad: PropTypes.string,
      episode: PropTypes.number,
      submitter: PropTypes.string,
      recordings: PropTypes.arrayOf(PropTypes.shape({
        recordingId: PropTypes.string,
        videoId: PropTypes.string,
        history: PropTypes.arrayOf(PropTypes.shape({
          recordingHistoryId: PropTypes.string,
          state: PropTypes.string,
          historyTime: PropTypes.string
        }))
      }))
    })
  };

  render() {
    const { run } = this.props;
    return (
      <div>
        <RunTitle episode={run.episode} map={run.map} iwad={run.iwad} category={run.runCategory} runTime={run.runTime} runner={run.runner} />
        <RunVideo recordings={run.recordings} />
        <RecordingStatus recordings={run.recordings} />
        <RunDetails run={run} />
      </div>
    );
  }
}