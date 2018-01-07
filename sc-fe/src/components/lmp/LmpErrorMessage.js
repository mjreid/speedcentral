import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormControl, FormGroup, ControlLabel, Col, Row} from 'react-bootstrap';
import './Lmp.css'

export default class LmpErrorMessage extends Component {
  static propTypes = {
    errorMessage: PropTypes.string.required
  };

  render() {
    const { errorMessage } = this.props;
    return (
      <div className="Error">
        {errorMessage}
      </div>
    )
  }
}