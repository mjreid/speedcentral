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

  componentWillUnmount() {
    if (this.interval) {
      this.clearInterval(this.interval);
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