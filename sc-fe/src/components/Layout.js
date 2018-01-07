import React, {Component} from "react";
import {Grid, Row, Col} from 'react-bootstrap';
import LayoutHeader from "./LayoutHeader";
import "./Layout.css";

export class Layout extends Component {

  render() {
    return (
      <Grid className="LayoutContainer">
        <Row className="LayoutContainerHeader">
          <Col xs={12}>
            <LayoutHeader />
          </Col>
        </Row>
        <Row className="LayoutContainerContent">
          <Col xs={1} md={1} lg={1}>
            <div>
            </div>
          </Col>
          <Col xs={10} md={8}>
            <div>
              {this.props.children}
            </div>
          </Col>
          <Col xs={1} md={3}>
            <div>
            </div>
          </Col>
        </Row>
      </Grid>
    );
  }
}