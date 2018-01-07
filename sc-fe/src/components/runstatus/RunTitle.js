import React, { Component } from 'react';
import PropTypes from 'prop-types';
import * as Helpers from '../../services/helpers';

export default class RunTitle extends Component {

  static propTypes = {
    iwad: PropTypes.string,
    pwad: PropTypes.string,
    episode: PropTypes.string,
    map: PropTypes.string,
    runTime: PropTypes.string,
    runner: PropTypes.string,
    category: PropTypes.string,
    className: PropTypes.string,
  };

  render() {
    const { iwad, episode, pwad, map, runTime, runner, category } = this.props;
    let pwadFilename = undefined;
    if (pwad) {
      pwadFilename = pwad.pwadFilename;
    }
    const runTitle = Helpers.generateRunTitle(iwad, pwadFilename, map, episode, runTime, runner, category);
    return (<div className={this.props.className}>{runTitle}</div>);
  }
}