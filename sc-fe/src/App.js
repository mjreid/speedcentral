import React, { Component } from 'react';
import SearchPage from './containers/SearchPage';
import logo from './logo.svg';
import './App.css';
import {Grid, Row, Col, PageHeader} from "react-bootstrap";

class App extends Component {

  /*render() {
    return (
      <Grid>
        <Row className="show-grid">
          <Col xs={12}>
            <div>
              Top Navbar Goes Here
            </div>
          </Col>
        </Row>
        <Row className="showGrid">
          <Col xs={2}>
            <div>
              Sidebar Goes Here
            </div>
          </Col>
          <Col xs={10}>
            <div>
              Main Content. Hello!
              {this.props.children}
            </div>
          </Col>
        </Row>
      </Grid>
    )
  }*/

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to React</h1>
        </header>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>
        <SearchPage />
      </div>
    );
  }
}

export default App;
