import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';


export default class LmpRunner extends Component {

  static propTypes = {
    runner: PropTypes.string,
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
    const runner = event.target.value;
    this.props.onLmpDataChanged({runner: runner});
  }

  render() {
    return (
      <div>
        <FormGroup bsSize="sm" controlId="runner" className={this.props.groupClass}>
          <Col sm={this.props.labelSize} componentClass={ControlLabel}>
            Runner
          </Col>
          <Col sm={this.props.inputSize}>
            <FormControl id="runner" type="text" value={this.props.runner} onChange={this.handleChange} />
          </Col>
        </FormGroup>
      </div>
    );
  }
}