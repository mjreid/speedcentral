import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { FormGroup, ControlLabel, FormControl } from 'react-bootstrap';

export default class LmpEngineSelector extends Component {

  static propTypes = {
    engineVersion: PropTypes.string,
    onLmpDataChanged: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    const engineVersion = event.target.value;
    this.props.onLmpDataChanged({ engineVersion: engineVersion });
  }

  render() {
    return (
      <FormGroup controlId="category">
        <ControlLabel>Category</ControlLabel>
        <FormControl componentClass="select" value={this.props.engineVersion} onChange={this.handleChange} >
          <option value="doom2">Doom 1.9</option>
          <option value="prboom-plus">Prboom+</option>
        </FormControl>
      </FormGroup>
    );
  }
}