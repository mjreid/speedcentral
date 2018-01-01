import {call, put, takeEvery} from "redux-saga/effects";
import {api} from "../api";
import {SERVER_FAILED} from "../constants/messages";

const RUN_STATUS_REQUEST = 'sc-fe/runstatus/REQUEST';
const RUN_STATUS_SUCCESS = 'sc-fe/runstatus/SUCCESS';
const RUN_STATUS_FAILURE = 'sc-fe/runstatus/FAILURE';

const initialState = {
  run: {}
};

export function runStatusRequest(runId) {
  return { type: RUN_STATUS_REQUEST, runId };
}

export function runStatusSuccess(response) {
  return { type: RUN_STATUS_SUCCESS, response };
}

export function runStatusFailure(error) {
  return { type: RUN_STATUS_FAILURE, error };
}

export default function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case RUN_STATUS_SUCCESS:
      return {
        ...state,
        run: action.response
      };
    case RUN_STATUS_FAILURE:
      return {
        ...state,
        run: {},
        errorMessage: action.error
      };
    default:
      return state;
  }
}

function* getRunStatus(runStatusRequest) {
  try {
    const response = yield call(api.getRunStatusRequest, runStatusRequest.runId);
    yield put(runStatusSuccess(response));
  } catch(error) {
    yield put(runStatusFailure(SERVER_FAILED));
  }
}

export function* watchRunStatusRequest() {
  yield takeEvery(RUN_STATUS_REQUEST, getRunStatus);
}