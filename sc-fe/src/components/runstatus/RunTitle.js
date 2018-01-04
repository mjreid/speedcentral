import React, { Component } from 'react';
import PropTypes from 'prop-types';
import * as Helpers from '../../services/helpers';

export default class RunTitle extends Component {

  static propTypes = {
    iwad: PropTypes.string,
    episode: PropTypes.string,
    map: PropTypes.string,
    runTime: PropTypes.string,
    runner: PropTypes.string,
    category: PropTypes.string,
  };

  render() {
    const { iwad, episode, map, runTime, runner, category } = this.props;
    let runTitle = Helpers.generateRunTitle(iwad, map, episode, runTime, runner, category);
    return (<div>{runTitle}</div>);
  }
}