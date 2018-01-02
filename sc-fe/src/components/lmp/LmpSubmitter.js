import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';

export default class LmpSubmitter extends Component {

  static propTypes = {
    submitter: PropTypes.string,
    onLmpDataChanged: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    const submitter = event.target.value;
    this.props.onLmpDataChanged({submitter: submitter});
  }

  render() {
    return (
      <FormGroup controlId="submitter">
        <Col sm={2}>
          Submitter
        </Col>
        <Col sm={10}>
          <FormControl id="submitter" type="text" value={this.props.submitter} onChange={this.handleChange} />
        </Col>
      </FormGroup>
    );
  }
}