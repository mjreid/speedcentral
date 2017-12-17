import { combineReducers } from 'redux';
import searchReducer from '../modules/search';
import routerReducer from '../modules/router';

const rootReducer = combineReducers({
  routerReducer,
  searchReducer
});

export default rootReducer;