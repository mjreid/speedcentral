import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';


import registerServiceWorker from './registerServiceWorker';
import configureStore from './store/configureStore';
import bootstrapSaga from './sagas';
import routes from './routes';
import Root from "./containers/Root";
import { history } from './services';

const store = configureStore();
store.runSaga(bootstrapSaga);

ReactDOM.render(
  <Root
    store={store}
    history={history}
    routes={routes} />,
  document.getElementById('root')
);
registerServiceWorker();
