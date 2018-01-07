import React, { Component } from 'react';
import PropTypes from 'prop-types';
import RunVideo from "./RunVideo";
import RunTitle from "./RunTitle";
import RunDetails from "./RunDetails";
import RecordingStatus from "./RecordingStatus";
import "./Run.css";
import {Col, Grid, Row} from "react-bootstrap";

export default class RunWrapper extends Component {

  static propTypes = {
    run: PropTypes.shape({
      map: PropTypes.string,
      runCategory: PropTypes.string,
      runTime: PropTypes.string,
      modifiedDate: PropTypes.string,
      runner: PropTypes.string,
      skillLevel: PropTypes.number,
      iwad: PropTypes.string,
      episode: PropTypes.string,
      submitter: PropTypes.string,
      recordings: PropTypes.arrayOf(PropTypes.shape({
        recordingId: PropTypes.string,
        videoId: PropTypes.string,
        history: PropTypes.arrayOf(PropTypes.shape({
          recordingHistoryId: PropTypes.string,
          state: PropTypes.string,
          historyTime: PropTypes.string
        }))
      })),
      pwad: PropTypes.object
    })
  };

  render() {
    const { run } = this.props;
    return (
      <div className="LightGray BoundingBox">
        <Row>
          <Col xs={12}>
            <RunTitle episode={run.episode} map={run.map} iwad={run.iwad} category={run.runCategory} runTime={run.runTime} runner={run.runner} pwad={run.pwad}
                      className="text-nowrap CenteredText"
                      />
          </Col>
        </Row>
        <Row>
          <Col xs={12}>
            <RunVideo recordings={run.recordings} className="CenteredText" />
          </Col>
        </Row>
        <RecordingStatus recordings={run.recordings} />
        <RunDetails run={run} />
      </div>
    );
  }
}