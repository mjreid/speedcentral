import { combineReducers } from 'redux';
import * as ActionTypes from '../actions';

function router(state = { pathname: '/' }, action) {
  switch (action.type) {
    case ActionTypes.UPDATE_ROUTER_STATE:
      return action.state;
    default:
      return state;
  }
}

const defaultSearchResults = { query: "", paging: {}, results: [] };
function searchResults(state = defaultSearchResults, action) {
  switch (action.type) {
    case ActionTypes.SEARCH_SUCCESS:
      return {
        ...state,
        paging: {},
        query: action.query,
        results: action.response,
      };
    default:
      return state;
  }
}

const rootReducer = combineReducers({
  router,
  searchResults
});

export default rootReducer;