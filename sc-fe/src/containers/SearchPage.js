import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import SearchResults from '../components/SearchResults';
import { search } from '../actions';

class SearchPage extends Component {

  static propTypes = {
    query: PropTypes.string.isRequired,
    paging: PropTypes.object.isRequired,
    results: PropTypes.array.isRequired,
    searchRequest: PropTypes.func.isRequired,
  };

  static defaultProps = {
    query: "foo",
    paging: {},
    results: [],
  };

  componentWillMount() {
    const { searchRequest } = this.props;
    searchRequest("foo");
  }

  componentWillReceiveProps() {

  }

  render() {
    const { query, paging, results } = this.props;
    if (results.length === 0) {
      return (<div>Loading...</div>)
    } else {
      return (
        <SearchResults items={results} />
      )
    }
  }
}

function mapStateToProps(state) {
  return {
    query: state.searchResults.query,
    paging: state.searchResults.paging,
    results: state.searchResults.results,
  };
}

function mapDispatchToProps(dispatch) {
  return({
    searchRequest: (query) => dispatch(search.request(query))
  })
}

export default connect(mapStateToProps, mapDispatchToProps)(SearchPage);