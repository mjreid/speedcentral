import { combineReducers } from 'redux';
import searchReducer from '../modules/search';
import { routerReducer } from 'react-router-redux';
import feed from '../modules/feed';

const rootReducer = combineReducers({
  router: routerReducer,
  searchReducer,
  feed
});

export default rootReducer;