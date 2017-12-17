import {take} from "redux-saga/effects";
import {history} from "../services";

const UPDATE_ROUTER_STATE = "sc-fe/router/UPDATE";
const NAVIGATE = "sc-fe/router/NAVIGATE";

export function* watchNavigate() {
  while(true) {
    const { pathname } = yield take(NAVIGATE);
    yield history.push(pathname);
  }
}

export function navigate(pathname) {
  return { "type": NAVIGATE, pathname }
}

export default function reducer(state = { pathname: '/' }, action) {
  switch (action.type) {
    case UPDATE_ROUTER_STATE:
      return action.state;
    default:
      return state;
  }
}