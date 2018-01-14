import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';

export default class LmpCategorySelector extends Component {

  static propTypes = {
    category: PropTypes.string,
    onLmpDataChanged: PropTypes.func,
    groupClass: PropTypes.string.required,
    labelSize: PropTypes.number.required,
    inputSize: PropTypes.number.required
  };

  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    const category = event.target.value;
    this.props.onLmpDataChanged({ category: category });
  }

  render() {
    return (
      <FormGroup bsSize="sm" controlId="category" className={this.props.groupClass}>
        <Col componentClass={ControlLabel} sm={this.props.labelSize}>
          Category
        </Col>
        <Col sm={this.props.inputSize}>
          <FormControl componentClass="select" value={this.props.category} onChange={this.handleChange} >
            <option value="uv-max">UV Max</option>
            <option value="uv-speed">UV Speed</option>
            <option value="nm-speed">NM Speed</option>
            <option value="nm100">NM100</option>
            <option value="uv-fast">UV -fast</option>
          </FormControl>
        </Col>
      </FormGroup>
    );
  }
}