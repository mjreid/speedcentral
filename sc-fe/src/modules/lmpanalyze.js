import {call, put, takeEvery} from "redux-saga/effects";
import {api} from "../api";
import {SERVER_FAILED} from "../constants/messages";

// API call
const LMP_ANALYZE_REQUEST = 'sc-fe/lmpanalyze/REQUEST';
const LMP_ANALYZE_SUCCESS = 'sc-fe/lmpanalyze/SUCCESS';
const LMP_ANALYZE_FAILURE = 'sc-fe/lmpanalyze/FAILURE';
// Called internally
const LMP_ANALYZE_DATA_CHANGED = 'sc-fe/impanalyze/CHANGED';
// PWAD resolving
const PWAD_RESOLVE_REQUEST = 'sc-fe/pwadresolve/REQUEST';
const PWAD_RESOLVE_SUCCESS = 'sc-fe/pwadresolve/SUCCESS';
const PWAD_RESOLVE_FAILURE = 'sc-fe/pwadresolve/FAILURE';


const initialState = {
  analysisResult: {}
};

export function lmpAnalyzeRequest(lmp) {
  return { type: LMP_ANALYZE_REQUEST, lmp };
}

export function lmpAnalyzeSuccess(lmp, response) {
  return { type: LMP_ANALYZE_SUCCESS, lmp, response };
}

export function lmpAnalyzeFailure(lmp, error) {
  return { type: LMP_ANALYZE_FAILURE, lmp, error };
}

export function lmpAnalyzeDataChanged(updatedFields) {
  return { type: LMP_ANALYZE_DATA_CHANGED, updatedFields };
}

export function pwadResolveRequest(pwadFilename, iwad) {
  return { type: PWAD_RESOLVE_REQUEST, pwadFilename, iwad };
}

export function pwadResolveSuccess(pwadFilename, response) {
  return { type: PWAD_RESOLVE_SUCCESS, pwadFilename, response };
}

export function pwadResolveFailure(error) {
  return { type: PWAD_RESOLVE_FAILURE, error };
}

export default function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LMP_ANALYZE_SUCCESS:
      const response = fixMissingPrimaryPwad(action.response);
      return {
        ...state,
        analysisResult: Object.assign({}, response, { lmp: action.lmp, category: "uv-max" })
      };
    case LMP_ANALYZE_FAILURE:
      return {
        ...state,
        analysisResult: { lmp: action.lmp },
        errorMessage: action.error
      };
    case LMP_ANALYZE_DATA_CHANGED:
      return {
        ...state,
        analysisResult: Object.assign({}, state.analysisResult, action.updatedFields)
      };
    case PWAD_RESOLVE_SUCCESS:
      const { pwadFilename, pwadIdgamesLocation } = action.response;
      if (state.analysisResult.primaryPwad && state.analysisResult.primaryPwad.pwadFilename === pwadFilename) {
        return {
          ...state,
          analysisResult: Object.assign({}, state.analysisResult, { primaryPwad: { pwadFilename, pwadIdgamesLocation }})
        }
      } else if (state.analysisResult.secondaryPwads) {
        const updatedSecondaryPwads = state.analysisResult.secondaryPwads.map(function(pwad) {
          if (pwad.pwadFilename === pwadFilename) {
            return { pwadFilename, pwadIdgamesLocation };
          } else {
            return pwad;
          }
        });
        return {
          ...state,
          analysisResult: Object.assign({}, state.analysisResult, { secondaryPwads: updatedSecondaryPwads })
        }
      } else {
        return state;
      }

    case PWAD_RESOLVE_FAILURE:
      return {
        ...state,
        errorMessage: action.error
      };
    default:
      return state;
  }
}

function fixMissingPrimaryPwad(lmpData) {
  if (!lmpData.primaryPwad) {
    return Object.assign({}, lmpData, { primaryPwad: { pwadFilename: "", pwadIdgamesLocation: "" } });
  }
  return lmpData;
}


function* analyzeLmp(lmpAnalyzeRequest) {
  try {
    const analysisResults = yield call(api.analyzeLmp, lmpAnalyzeRequest.lmp);
    yield put(lmpAnalyzeSuccess(lmpAnalyzeRequest.lmp, analysisResults));
  } catch(error) {
    yield put(lmpAnalyzeFailure(lmpAnalyzeRequest.lmp, SERVER_FAILED));
  }
}


function* resolvePwad(pwadResolveRequest) {
  try {
    const resolveResults = yield call(api.resolvePwad, pwadResolveRequest.pwadFilename, pwadResolveRequest.iwad);
    yield put(pwadResolveSuccess(pwadResolveRequest.pwadFilename, resolveResults));
  } catch(error) {
    yield put(pwadResolveFailure(SERVER_FAILED));
  }
}


export function* watchAnalyzeRequest() {
  yield takeEvery(LMP_ANALYZE_REQUEST, analyzeLmp);
}

export function* watchPwadResolveRequest() {
  yield takeEvery(PWAD_RESOLVE_REQUEST, resolvePwad);
}