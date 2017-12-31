import React, {Component} from "react";
import {Grid, Row, Col} from 'react-bootstrap';
import {LinkContainer} from "react-router-bootstrap";
import Button from "react-bootstrap/es/Button";

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
              <LinkContainer to="/lmpupload">
                <Button>
                  LMP Upload
                </Button>
              </LinkContainer>
            </div>
          </Col>
          <Col xs={10}>
            <div>
              {this.props.children}
            </div>
          </Col>
        </Row>
      </Grid>
    );
  }
}