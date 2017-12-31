import {call, put, takeEvery} from "redux-saga/effects";
import {api} from "../api";
import {SERVER_FAILED} from "../constants/messages";

const DEMO_SUBMIT_REQUEST = 'sc-fe/demosubmit/REQUEST';
const DEMO_SUBMIT_SUCCESS = 'sc-fe/demosubmit/SUCCESS';
const DEMO_SUBMIT_FAILURE = 'sc-fe/demosubmit/FAILURE';

const initialState = {
  demoId: NaN
};

export function demoSubmitRequest(lmpData) {
  return { type: DEMO_SUBMIT_REQUEST, lmpData };
}

export function demoSubmitSuccess(response) {
  return { type: DEMO_SUBMIT_SUCCESS, response };
}

export function demoSubmitFailure(error) {
  return { type: DEMO_SUBMIT_FAILURE, error };
}

export default function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case DEMO_SUBMIT_SUCCESS:
      return {
        ...state,
        demoId: action.response.demoId
      };
    case DEMO_SUBMIT_FAILURE:
      return {
        ...state,
        errorMessage: action.error
      };
    default:
      return state;
  }
}

function* submitDemo(demoRequest) {
  try {
    const demoSubmissionResult = yield call(api.submitRun, demoRequest.lmpData);
    yield put(demoSubmitSuccess(demoSubmissionResult));
  } catch(error) {
    yield put(demoSubmitFailure(SERVER_FAILED));
  }
}

export function* watchDemoSubmitRequest() {
  yield takeEvery(DEMO_SUBMIT_REQUEST, submitDemo);
}