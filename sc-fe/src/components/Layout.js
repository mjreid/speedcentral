import React, {Component} from "react";
import {Grid, Row, Col} from 'react-bootstrap';
import {LinkContainer} from "react-router-bootstrap";
import Button from "react-bootstrap/es/Button";
import LayoutHeader from "./LayoutHeader";

export class Layout extends Component {

  render() {
    return (
      <Grid>
        <Row className="show-grid">
          <Col xs={12}>
            <LayoutHeader />
          </Col>
        </Row>
        <Row className="showGrid">
          <Col xs={1} md={1} lg={1}>
            <div>
            </div>
          </Col>
          <Col xs={10} md={8} lg={6}>
            <div>
              {this.props.children}
            </div>
          </Col>
          <Col xs={1} md={3} lg={5}>
            <div>
            </div>
          </Col>
        </Row>
      </Grid>
    );
  }
}