import {call, put, takeEvery} from "redux-saga/effects";
import {api} from "../api";
import {SERVER_FAILED} from "../constants/messages";

const SEARCH_REQUEST = 'sc-fe/search/REQUEST';
const SEARCH_SUCCESS = 'sc-fe/search/SUCCESS';
const SEARCH_FAILURE = 'sc-fe/search/FAILURE';

const initialState = { query: "", paging: {}, results: [] };

export function searchRequest(query) {
  return { type: SEARCH_REQUEST, query };
}

export function searchSuccess(query, response) {
  return { type: SEARCH_SUCCESS, query, response };
}

export function searchFailure(query, error) {
  return { type: SEARCH_FAILURE, query, error };
}

export default function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case SEARCH_SUCCESS:
      return {
        ...state,
        paging: {},
        query: action.query,
        results: action.response,
      };
    case SEARCH_FAILURE:
      return {
        ...state,
        query: action.query,
        errorMessage: action.error
      };
    default:
      return state;
  }
}

function* search(searchRequestAction) {
  try {
    const searchResults = yield call(api.search, searchRequestAction.query);
    yield put(searchSuccess(searchRequestAction.query, searchResults));
  } catch(error) {
    yield put(searchFailure(searchRequestAction.query, SERVER_FAILED));
  }
}

export function* watchSearchRequest() {
  yield takeEvery(SEARCH_REQUEST, search);
}