import { fork, all } from 'redux-saga/effects';
import {watchSearchRequest} from "../modules/search";
import {watchNavigate} from "../modules/router";

export default function* root() {
  yield all([
    fork(watchNavigate),
    fork(watchSearchRequest)
  ]);
}