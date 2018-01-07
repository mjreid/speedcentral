import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';

export default class LmpSubmitter extends Component {

  static propTypes = {
    submitter: PropTypes.string,
    onLmpDataChanged: PropTypes.func,
    groupClass: PropTypes.string.required,
    labelSize: PropTypes.number.required,
    inputSize: PropTypes.number.required,
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
      <div>
        <FormGroup bsSize="sm" controlId="submitter" className={this.props.groupClass}>
          <Col sm={this.props.labelSize} componentClass={ControlLabel}>
            Submitter
          </Col>
          <Col sm={this.props.inputSize}>
            <FormControl type="text" value={this.props.submitter} onChange={this.handleChange} />
          </Col>
        </FormGroup>
      </div>
    );
  }
}