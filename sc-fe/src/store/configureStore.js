import {applyMiddleware, compose, createStore} from "redux";
import DevTools from "../containers/DevTools";
import {END} from "redux-saga";
import {routerMiddleware} from "react-router-redux";
import createSagaMiddleware from "redux-saga";
import {createLogger} from "redux-logger";
import rootReducer from "../reducers";

export default function configureStore(initialState, history) {

  const isProd = (process.env.NODE_ENV === 'production');

  const sagaMiddleware = createSagaMiddleware();
  if (!isProd) {
    const store = createStore(
      rootReducer,
      initialState,
      compose(
        applyMiddleware(
          routerMiddleware(history),
          sagaMiddleware,
          createLogger()
        ),
        DevTools.instrument()
      )
    );

    if (module.hot) {
      // Enable Webpack hot module replacement for reducers
      module.hot.accept('../reducers', () => {
        const nextRootReducer = require('../reducers').default;
        store.replaceReducer(nextRootReducer)
      });
    }
    store.runSaga = sagaMiddleware.run;
    store.close = () => store.dispatch(END);
    return store;
  } else {
    const sagaMiddleware = createSagaMiddleware();
    const store = createStore(
      rootReducer,
      initialState,
      applyMiddleware(sagaMiddleware, routerMiddleware(history))
    );

    store.runSaga = sagaMiddleware.run;
    store.close = () => store.dispatch(END);
    return store;
  }
}