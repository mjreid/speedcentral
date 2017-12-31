import {call, put, takeEvery} from "redux-saga/effects";
import {api} from "../api";
import {SERVER_FAILED} from "../constants/messages";

// API call
const LMP_ANALYZE_REQUEST = 'sc-fe/lmpanalyze/REQUEST';
const LMP_ANALYZE_SUCCESS = 'sc-fe/lmpanalyze/SUCCESS';
const LMP_ANALYZE_FAILURE = 'sc-fe/lmpanalyze/FAILURE';
// Called internally
const LMP_ANALYZE_DATA_CHANGED = 'sc-fe/impanalyze/CHANGED';

const initialState = {
  analysisResult: {}
};

export function lmpAnalyzeRequest(lmp) {
  return { type: LMP_ANALYZE_REQUEST, lmp };
}

export function lmpAnalyzeSuccess(response) {
  return { type: LMP_ANALYZE_SUCCESS, response };
}

export function lmpAnalyzeFailure(error) {
  return { type: LMP_ANALYZE_FAILURE, error };
}

export function lmpAnalyzeDataChanged(updatedFields) {
  return { type: LMP_ANALYZE_DATA_CHANGED, updatedFields };
}

export default function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LMP_ANALYZE_SUCCESS:
      return {
        ...state,
        analysisResult: action.response
      };
    case LMP_ANALYZE_FAILURE:
      return {
        ...state,
        errorMessage: action.error
      };
    case LMP_ANALYZE_DATA_CHANGED:
      return {
        ...state,
        analysisResult: Object.assign({}, state.analysisResult, action.updatedFields)
      };
    default:
      return state;
  }
}

function* analyzeLmp(lmpAnalyzeRequest) {
  try {
    const analysisResults = yield call(api.analyzeLmp, lmpAnalyzeRequest.lmp);
    yield put(lmpAnalyzeSuccess(analysisResults));
  } catch(error) {
    yield put(lmpAnalyzeFailure(SERVER_FAILED));
  }
}

export function* watchAnalyzeRequest() {
  yield takeEvery(LMP_ANALYZE_REQUEST, analyzeLmp);
}