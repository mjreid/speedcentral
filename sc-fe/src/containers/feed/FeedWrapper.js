import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import FeedLoading from "../../components/feed/FeedLoading";
import FeedEntryMessage from "../../components/feed/FeedEntryMessage";
import {feedRequest} from "../../modules/feed";

const FEED_MESSAGE = "message";
const FEED_SPEEDRUN = "speedrun";
const FEED_HIGHLIGHT = "highlight";

class FeedWrapper extends Component {

  static propTypes = {
    feedEntries: PropTypes.array,
    feedRequest: PropTypes.func.isRequired,
  };

  static defaultProps = {
    feedEntries: [],
  };

  componentWillMount() {
    const { feedRequest } = this.props;
    feedRequest();
  }

  render() {
    const { feedEntries } = this.props;
    if (feedEntries.length === 0) {
      return (<FeedLoading />)
    } else {
      return (<div>
        {
          feedEntries.map(function (feedEntry) {
            if (feedEntry.itemType === FEED_MESSAGE) {
              return (<FeedEntryMessage id={feedEntry.id} date={feedEntry.date} data={feedEntry.data} />);
            } else if (feedEntry.itemType === FEED_SPEEDRUN) {
              return (<div />);
            } else if (feedEntry.itemType === FEED_HIGHLIGHT) {
              return (<div />);
            } else {
              console.debug("Unknown feed item type " + feedEntry.itemType);
              return (<div />);
            }
          })
        }
      </div>);
    }
  }
}

function mapStateToProps(state) {
  return {
    feedEntries: state.feed.query,
  };
}

function mapDispatchToProps(dispatch) {
  return({
    feedRequest: (query) => dispatch(feedRequest(query))
  });
}

export default connect(mapStateToProps, mapDispatchToProps)(FeedWrapper);