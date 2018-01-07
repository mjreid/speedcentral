import React, { Component } from 'react';
import PropTypes from 'prop-types';
import "./Run.css";

export default class RunVideo extends Component {

  static propTypes = {
    recordings: PropTypes.arrayOf(PropTypes.shape({
      videoId: PropTypes.string
    })),
    className: PropTypes.string,
  };

  render() {
    const { recordings } = this.props;

    const noVideo = (
      <div className={this.props.className}>
        <div className="EmptyVideo">
          <div className="spinner">
            <div className="rect1" />
            <div className="rect2" />
            <div className="rect3" />
            <div className="rect4" />
            <div className="rect5" />
          </div>
        </div>
      </div>
    );


    if (recordings.length > 0) {
      const recording = recordings[0];
      if (recording.videoId) {
        const youTubeEmbedUrl = `https://www.youtube.com/embed/${recording.videoId}?autoplay=0`;
        return (
          <div className={this.props.className}>
            <iframe width="420" height="315" src={youTubeEmbedUrl} />
          </div>
        );
      } else {
        return noVideo;
      }
    } else {
      return noVideo;
    }
  }
}