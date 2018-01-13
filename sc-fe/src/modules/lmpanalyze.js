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
const INVALID_DEMO_FORMAT = 'Could not analyze the LMP. Currently supported formats are Doom (2), Boom, and PRBoom.';

const initialState = {
  analysisResult: {},
  errorMessage: undefined
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

export function pwadResolveFailure(pwadFilename, error) {
  return { type: PWAD_RESOLVE_FAILURE, errorPwadFilename: pwadFilename, error };
}

export default function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LMP_ANALYZE_SUCCESS:
      const response = fixMissingPrimaryPwad(action.response);
      return {
        ...state,
        analysisResult: Object.assign({}, response, { lmp: action.lmp, category: "uv-max" }),
        errorMessage: undefined
      };
    case LMP_ANALYZE_FAILURE:
      return {
        ...state,
        analysisResult: {},
        errorMessage: action.error
      };
    case LMP_ANALYZE_DATA_CHANGED:
      return {
        ...state,
        analysisResult: Object.assign({}, state.analysisResult, action.updatedFields)
      };
    case PWAD_RESOLVE_SUCCESS:
      const { pwadFilename, pwadIdgamesLocation } = action.response;
      const maybePrimaryPwad = maybeUpdatedPrimaryPwad(state, pwadFilename, pwadIdgamesLocation);
      if (maybePrimaryPwad) {
        return {
          ...state,
          analysisResult: Object.assign({}, state.analysisResult, { primaryPwad: maybePrimaryPwad })
        };
      } else {
        const maybeSecondaryPwads = maybeUpdatedSecondaryPwads(state, pwadFilename, pwadIdgamesLocation);
        if (maybeSecondaryPwads) {
          return {
            ...state,
            analysisResult: Object.assign({}, state.analysisResult, {secondaryPwads: maybeSecondaryPwads})
          };
        } else {
          return state;
        }
      }

    case PWAD_RESOLVE_FAILURE:
      const { errorPwadFilename, error } = action;
      const maybeErrorPwad = maybeUpdatedPrimaryPwad(state, errorPwadFilename, "", "Could not find PWAD");
      if (maybeErrorPwad) {
        return {
          ...state,
          analysisResult: Object.assign({}, state.analysisResult, { primaryPwad: maybeErrorPwad })
        };
      } else {
        return {
          ...state,
          errorMessage: action.error
        };
      }
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

// Returns a new primary PWAD object if the pwadFilename matches the primary pwad in the state.
// Otherwise returns null.
function maybeUpdatedPrimaryPwad(state, pwadFilename, pwadIdgamesLocation, errorMessage = undefined) {
  if (state.analysisResult.primaryPwad && (state.analysisResult.primaryPwad.pwadFilename === pwadFilename
      || state.analysisResult.primaryPwad.pwadFilename === pwadFilename + ".wad"
      || state.analysisResult.primaryPwad.pwadFilename === pwadFilename + ".zip")) {
    return { pwadFilename, pwadIdgamesLocation, errorMessage };
  } else {
    return null;
  }
}

// Returns a new list of secondary PWADS if the pwadFilename matches any of the secondary pwads.
// Otherwise returns null.
function maybeUpdatedSecondaryPwads(state, pwadFilename, pwadIdgamesLocation, errorMessage = undefined) {
  if (state.analysisResult.secondaryPwads) {
    return state.analysisResult.secondaryPwads.map(function(pwad) {
      if (pwad.pwadFilename === pwadFilename
        || pwad.pwadFilename === pwadFilename + ".wad"
        || pwad.pwadFilename === pwadFilename + ".zip") {
        return { pwadFilename, pwadIdgamesLocation, errorMessage };
      } else {
        return pwad;
      }
    });
  } else {
    return null;
  }
}

function* analyzeLmp(lmpAnalyzeRequest) {
  try {
    const analysisResults = yield call(api.analyzeLmp, lmpAnalyzeRequest.lmp);
    yield put(lmpAnalyzeSuccess(lmpAnalyzeRequest.lmp, analysisResults));
  } catch(error) {
    yield put(lmpAnalyzeFailure(lmpAnalyzeRequest.lmp, INVALID_DEMO_FORMAT));
  }
}


function* resolvePwad(pwadResolveRequest) {
  try {
    const resolveResults = yield call(api.resolvePwad, pwadResolveRequest.pwadFilename, pwadResolveRequest.iwad);
    yield put(pwadResolveSuccess(pwadResolveRequest.pwadFilename, resolveResults));
  } catch(error) {
    yield put(pwadResolveFailure(pwadResolveRequest.pwadFilename, SERVER_FAILED));
  }
}


export function* watchAnalyzeRequest() {
  yield takeEvery(LMP_ANALYZE_REQUEST, analyzeLmp);
}

export function* watchPwadResolveRequest() {
  yield takeEvery(PWAD_RESOLVE_REQUEST, resolvePwad);
}