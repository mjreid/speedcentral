import React, { Component } from 'react';
import PropTypes from 'prop-types';

export default class RunVideo extends Component {

  static propTypes = {
    recordings: PropTypes.arrayOf(PropTypes.shape({
      videoId: PropTypes.string
    }))
  };

  render() {
    const { recordings } = this.props;
    if (recordings.length > 0) {
      const recording = recordings[0];
      if (recording.videoId) {
        const youTubeEmbedUrl = `https://www.youtube.com/embed/${recording.videoId}?autoplay=0`;
        return (
          <iframe width="420" height="315" src={youTubeEmbedUrl} />
        );
      } else {
        return (<div />);
      }
    } else {
      return (<div />);
    }
  }
}