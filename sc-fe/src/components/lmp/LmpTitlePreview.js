import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {Col, FormGroup, Row, FormControl } from "react-bootstrap";
import { generateRunTitle } from '../../services/helpers';

export default class LmpTitlePreview extends Component {
  static propTypes = {
    className: PropTypes.string.isRequired,
    lmpData: PropTypes.object.isRequired,
  };

  buildTitle() {
    const { lmpData } = this.props;
    return generateRunTitle(lmpData.iwad, lmpData.primaryPwad.pwadFilename, lmpData.map, lmpData.episode, lmpData.runTime, lmpData.runner, lmpData.category);
  }

  render() {
    return (
      <FormGroup bsSize="sm" className={this.props.className}>
        <Col sm={2}>
        </Col>
        <Col sm={10}>
          <FormControl.Static>
            {this.buildTitle()}
          </FormControl.Static>
        </Col>
        <Col sm={2}>
        </Col>
      </FormGroup>
    );
  }
}