import { combineReducers } from 'redux';
import searchReducer from '../modules/search';
import { routerReducer } from 'react-router-redux';
import feed from '../modules/feed';
import lmpAnalysis from '../modules/lmpanalyze';
import demosubmit from '../modules/demosubmit';

const rootReducer = combineReducers({
  router: routerReducer,
  searchReducer,
  feed,
  lmpAnalysis,
  demosubmit
});

export default rootReducer;