import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';
import "./Lmp.css"

export default class LmpRuntimeSelector extends Component {

  static propTypes = {
    runTimeIso: PropTypes.string.required,
    onLmpDataChanged: PropTypes.func.required,
    groupClass: PropTypes.string.required,
    labelSize: PropTypes.number.required,
    inputSize: PropTypes.number.required,
  };

  constructor(props) {
    super(props);
    this.onBlur = this.onBlur.bind(this);
  }

  onBlur(event) {
    const runTime = event.target.value;
    this.props.onLmpDataChanged({ runTime: runTime });
  }


  render() {
    return (
      <div>
        <FormGroup bsSize="sm" controlId="runtime" className={this.props.groupClass}>
          <Col sm={this.props.labelSize} componentClass={ControlLabel}>
            Run Time
          </Col>
          <Col sm={this.props.inputSize}>
            <FormControl type="text" value={this.props.runTime} onBlur={this.onBlur} placeholder="HH:MM:SS"/>
          </Col>
        </FormGroup>
      </div>
    );
  }
}