import { fork, all } from 'redux-saga/effects';
import {watchSearchRequest} from "../modules/search";
import {watchFeedRequest} from '../modules/feed';
import {watchAnalyzeRequest} from "../modules/lmpanalyze";
import {watchDemoSubmitRequest} from "../modules/demosubmit";

export default function* root() {
  yield all([
    fork(watchSearchRequest),
    fork(watchFeedRequest),
    fork(watchAnalyzeRequest),
    fork(watchDemoSubmitRequest)
  ]);
}