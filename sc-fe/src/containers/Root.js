import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Provider } from 'react-redux';
import { ConnectedRouter as Router } from 'react-router-redux';
import {DevToolsWrapper} from './DevToolsWrapper';
import { Layout } from "../components/Layout";
import App from "../App";
import SearchPage from "./SearchPage";
import {Route} from "react-router-dom";
import FeedWrapper from "./feed/FeedWrapper";
import LmpUploadPage from "./LmpUploadPage";
import RunStatusPage from "./RunStatusPage";

export default class Root extends Component {
  render() {
    const { store, history } = this.props;
    return (
      <Provider store={store}>
        <div>
          <Router history={history}>
            <div>
              <Layout>
                <Route exact path="/" component={LmpUploadPage} key="1" />
                <Route path="/run/:runId" component={RunStatusPage} key="2" />
              </Layout>
            </div>
          </Router>
          <DevToolsWrapper />
        </div>
      </Provider>
    );
  }
}

Root.propTypes = {
  store: PropTypes.object.isRequired,
  history: PropTypes.object.isRequired,
};
