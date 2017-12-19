import { combineReducers } from 'redux';
import searchReducer from '../modules/search';
// import routerReducer from '../modules/router';
import { routerReducer } from 'react-router-redux';

const rootReducer = combineReducers({
  router: routerReducer,
  searchReducer
});

export default rootReducer;