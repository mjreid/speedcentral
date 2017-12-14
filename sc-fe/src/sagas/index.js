import { take, put, call, fork, takeEvery, all } from 'redux-saga/effects';
import { history } from '../services';
import * as actions from '../actions';
import { api } from '../api';
import {SEARCH_REQUEST} from "../actions";

function* watchNavigate() {
  while(true) {
    const { pathname } = yield take(actions.NAVIGATE);
    yield history.push(pathname);
  }
}

export function* search(searchRequestAction) {
  const searchResults = yield call(api.search, searchRequestAction.query);
  yield put(actions.search.success(searchRequestAction.query, searchResults))
}

function* watchSearchRequest() {
  yield takeEvery(SEARCH_REQUEST, search);
}

export default function* root() {
  yield all([
    fork(watchNavigate),
    fork(watchSearchRequest)
  ]);
}