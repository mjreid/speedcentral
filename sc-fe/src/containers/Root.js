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

export default class Root extends Component {
  render() {
    const { store, history } = this.props;
    return (
      <Provider store={store}>
        <div>
          <Router history={history}>
            <div>
              <Layout>
                <Route exact path="/" component={App} key="1" />
                <Route path="/news" component={FeedWrapper} key="2" />
                <Route path="/search" component={SearchPage} key="3" />
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
