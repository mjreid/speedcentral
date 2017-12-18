import PropTypes from "prop-types";
import React, {Component} from "react";
import {Grid, Row, Col} from 'react-bootstrap';
import {connect} from "react-redux";
import {LinkContainer} from "react-router-bootstrap";
import Button from "react-bootstrap/es/Button";

class Layout extends Component {

  static propTypes = {
    mainContent: PropTypes.func.isRequired
  };

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
              {this.props.mainContent()}
            </div>
          </Col>
        </Row>
      </Grid>
    );
  }
}

function mapStateToProps() {
  return {};
}

function mapDispatchToProps() {
  return {};
}

export default connect(mapStateToProps, mapDispatchToProps)(Layout);