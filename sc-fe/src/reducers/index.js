import { combineReducers } from 'redux';
import searchReducer from '../modules/search';
import { routerReducer } from 'react-router-redux';
import feed from '../modules/feed';
import lmpAnalysis from '../modules/lmpanalyze';

const rootReducer = combineReducers({
  router: routerReducer,
  searchReducer,
  feed,
  lmpAnalysis
});

export default rootReducer;