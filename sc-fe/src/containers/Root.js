import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Provider } from 'react-redux';
import { Router, Route } from 'react-router-dom';
import {DevToolsWrapper} from './DevToolsWrapper';
import App from "../App"
import {LinkContainer} from "react-router-bootstrap";
import {Button, PageHeader} from "react-bootstrap";
import SearchPage from "./SearchPage";
import Layout from "./Layout";

export default class Root extends Component {
  render() {
    const { store, history } = this.props;

    return (
      <Provider store={store}>
        <div>
          <Router history={history}>
            <div>
              <Layout mainContent={this.mainArea} />
            </div>
          </Router>
          <DevToolsWrapper />
        </div>
      </Provider>
    );
  }

  mainArea() {
    return (
      <div>
        <Route exact path="/" component={App} key="1" />
        <Route path="/news" component={News} key="2" />
        <Route path="/search" component={SearchPage} key="3" />
      </div>
    );
  }
}




class TopNavBar extends Component {
  render() {
    return (
      <div>
        <LinkContainer to="/search">
          <Button>Search</Button>
        </LinkContainer>
      </div>
    );
  }
}

class News extends Component {
  render() {
    return (
      <div>
        <PageHeader>Welcome to Speed Central!</PageHeader>
      </div>
    );
  }
}

Root.propTypes = {
  store: PropTypes.object.isRequired,
  history: PropTypes.object.isRequired,
};
