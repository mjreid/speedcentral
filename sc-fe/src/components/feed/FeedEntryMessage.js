import React, { Component } from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';

export default class FeedEntryMessage extends Component {

  static propTypes = {
    id: PropTypes.string.isRequired,
    date: PropTypes.string.isRequired,
    data: PropTypes.shape({
      header: PropTypes.string,
      content: PropTypes.string,
    }),
  };

  render() {
    const dateString = this.props.date;
    const { header, content } = this.props.data;

    const displayDate = moment(dateString).fromNow();

    return (<div>
      <div>{header} - {displayDate}</div>
      <div>{content}</div>
    </div>);
  }
}