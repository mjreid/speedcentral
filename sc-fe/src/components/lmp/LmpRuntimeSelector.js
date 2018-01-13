import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';
import "./Lmp.css"

export default class LmpRuntimeSelector extends Component {

  static propTypes = {
    runTime: PropTypes.string.required,
    onLmpDataChanged: PropTypes.func.required,
    groupClass: PropTypes.string.required,
    labelSize: PropTypes.number.required,
    inputSize: PropTypes.number.required,
  };

  constructor(props) {
    super(props);
    this.onBlur = this.onBlur.bind(this);
    this.onChange = this.onChange.bind(this);
  }

  onChange(event) {
    const runTime = event.target.value;
    this.props.onLmpDataChanged({ runTime: runTime });
  }

  onBlur(event) {
    let runTime = event.target.value;
    // Try to convert non-HH:MM:SS formats to HH:MM:SS (e.g. 4:56 -> 00:04:56)
    if (runTime) {
      runTime = runTime.replace(/\D/g, '');
      const zeroesToPad = 6 - runTime.length;
      let zeroes = "";
      if (zeroesToPad > 0) {
        zeroes = '0'.repeat(zeroesToPad);
      }
      runTime = zeroes + runTime;
      runTime = runTime[0] + runTime[1] + ":" + runTime[2] + runTime[3] + ":" + runTime[4] + runTime[5];
    }
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
            <FormControl type="text" value={this.props.runTime} onBlur={this.onBlur} onChange={this.onChange} placeholder="HH:MM:SS"/>
          </Col>
        </FormGroup>
      </div>
    );
  }
}