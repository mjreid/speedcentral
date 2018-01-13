import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';


import registerServiceWorker from './registerServiceWorker';
import configureStore from './store/configureStore';
import bootstrapSaga from './sagas';
import Root from "./containers/Root";
import { history } from './services';

const store = configureStore({}, history);
store.runSaga(bootstrapSaga);

ReactDOM.render(
  <Root
    store={store}
    history={history} />,
  document.getElementById('root')
);
