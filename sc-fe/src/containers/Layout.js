import React, {Component} from "react";
import {Grid, Row, Col} from 'react-bootstrap';
import {LinkContainer} from "react-router-bootstrap";
import Button from "react-bootstrap/es/Button";
import App from "../App";
import SearchPage from "./SearchPage";
import {Route} from "react-router-dom";
import {News} from "./News";

export class Layout extends Component {

  render() {
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
              <LinkContainer to="/">
                <Button>
                  Home
                </Button>
              </LinkContainer>
              <LinkContainer to="/news">
                <Button>
                  News
                </Button>
              </LinkContainer>
              <LinkContainer to="/search">
                <Button>
                  Search
                </Button>
              </LinkContainer>
            </div>
          </Col>
          <Col xs={10}>
            <div>
              <Route exact path="/" component={App} key="1" />
              <Route path="/news" component={News} key="2" />
              <Route path="/search" component={SearchPage} key="3" />
            </div>
          </Col>
        </Row>
      </Grid>
    );
  }
}