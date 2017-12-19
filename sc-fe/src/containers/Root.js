import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Provider } from 'react-redux';
import {Route, Switch} from 'react-router-dom';
import { ConnectedRouter as Router } from 'react-router-redux';
import {DevToolsWrapper} from './DevToolsWrapper';
import {LinkContainer} from "react-router-bootstrap";
import {Button, PageHeader} from "react-bootstrap";
import { Layout } from "./Layout";

export default class Root extends Component {
  render() {
    const { store, history } = this.props;
//<Layout mainContent={this.mainArea} />
    return (
      <Provider store={store}>
        <div>
          <Router history={history}>
            <div>
              <Switch>
                <Layout />
              </Switch>
            </div>
          </Router>
          <DevToolsWrapper />
        </div>
      </Provider>
    );
  }

  mainArea() {
    return;
    /*return (
      <Switch>
        <Route exact path="/" component={App} key="1" />
        <Route path="/news" component={News} key="2" />
        <Route path="/search" component={SearchPage} key="3" />
      </Switch>
    );*/
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

Root.propTypes = {
  store: PropTypes.object.isRequired,
  history: PropTypes.object.isRequired,
};
