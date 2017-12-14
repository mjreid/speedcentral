import PropTypes from 'prop-types';
import React, { Component } from 'react';

export default class SearchResults extends Component {

  render() {
    const { items } = this.props;
    return (
      <div>
        {items.map(SearchResults.renderItem)}
      </div>
    );
  }

  static renderItem(item) {
    return (
      <div>{item.name} - {item.id}</div>
    )
  }
}

SearchResults.propTypes = {
  items: PropTypes.array.isRequired,
};