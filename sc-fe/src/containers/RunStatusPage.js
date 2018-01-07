import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import {runStatusRequest} from "../modules/runstatus";
import RunWrapper from "../components/runstatus/RunWrapper";

class RunStatusPage extends Component {
  static propTypes = {
    run: PropTypes.object,
  };

  static defaultProps = {
    run: {},
  };

  componentWillMount() {
    const { runId } = this.props.match.params;
    const { runStatusRequest } = this.props;
    runStatusRequest(runId);
    this.interval = setInterval(function () { runStatusRequest(runId); }, 2000);
  }

  componentWillReceiveProps(nextProps) {
    // If terminal state, clear the interval
    if (nextProps.run && nextProps.run.recordings && nextProps.run.recordings.length > 0) {
      const recording = nextProps.run.recordings[0];
      if (recording.history && recording.history.length > 0) {
        const history = recording.history[0];
        if (this.interval && this.isTerminalState(history.state)) {
          clearInterval(this.interval);
        }
      }
    }
  }

  isTerminalState(state) {
    return (state === "upload_succeeded" || state === "pwad_resolve_failed"
      || state === "exe_recording_failed" || state === "upload_failed");
  }

  componentWillUnmount() {
    if (this.interval) {
      clearInterval(this.interval);
    }
  }

  render() {
    // If we don't have a run, say we're loading
    const { run } = this.props;
    if (Object.keys(run).length === 0) {
      return (
        <div>
          Loading...
        </div>
      );
    } else {
      return (
        <div>
          <RunWrapper run={run} />
        </div>
      );
    }
  }
}

function mapStateToProps(state) {
  return {
    run: state.runStatus.run
  };
}

function mapDispatchToProps(dispatch) {
  return({
    runStatusRequest: (runId) => dispatch(runStatusRequest(runId)),
  });
}

export default connect(mapStateToProps, mapDispatchToProps)(RunStatusPage);