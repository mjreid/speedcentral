import {call, put, takeEvery} from "redux-saga/effects";
import {api} from "../api";
import {SERVER_FAILED} from "../constants/messages";


const FEED_REQUEST = 'sc-fe/feed/REQUEST';
const FEED_SUCCESS = 'sc-fe/feed/SUCCESS';
const FEED_FAILURE = 'sc-fe/feed/FAILURE';

const initialState = { feedEntries: [] };

export function feedRequest(query) {
  return { type: FEED_REQUEST };
}

export function feedSuccess(response) {
  return { type: FEED_SUCCESS, response };
}

export function feedFailure(error) {
  return { type: FEED_FAILURE, error };
}

export default function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case FEED_SUCCESS:
      return {
        ...state,
        feedEntries: action.response,
      };
    case FEED_FAILURE:
      return {
        ...state,
        errorMessage: action.error
      };
    default:
      return state;
  }
}

function* loadFeed(searchRequestAction) {
  try {
    const feedResults = yield call(api.feed);
    yield put(feedSuccess(feedResults));
  } catch(error) {
    yield put(feedFailure(SERVER_FAILED));
  }
}

export function* watchFeedRequest() {
  yield takeEvery(FEED_REQUEST, loadFeed);
}