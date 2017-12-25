import React, { Component } from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';

export default class FeedEntrySpeedrun extends Component {

  static propTypes = {
    id: PropTypes.string.isRequired,
    date: PropTypes.string.isRequired,
    data: PropTypes.shape({
      runner: PropTypes.string,
      game: PropTypes.string,
      runTime: PropTypes.string,
    }),
  };

  render() {
    const runTimeString = this.props.data.runTime;
    const dateString = this.props.date;
    const { runner, game } = this.props.data;
    const runTimeDuration = moment.duration(runTimeString);

    console.log(runTimeString);
    const displayRunTime = runTimeDuration.minutes().toString() + "m" + runTimeDuration.seconds().toString() + "s" + runTimeDuration.milliseconds().toString() + "ms";
    const displayDate = moment(dateString).fromNow();

    return (<div>
      <div>{runner} {game} in {displayRunTime} - {displayDate}</div>
      <div>Congrats.</div>
    </div>);
  }
}