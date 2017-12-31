import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { FormGroup, ControlLabel, FormControl } from 'react-bootstrap';

export default class LmpCategorySelector extends Component {

  static propTypes = {
    category: PropTypes.string,
    onLmpDataChanged: PropTypes.func,
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
      <FormGroup controlId="category">
        <ControlLabel>Category</ControlLabel>
        <FormControl componentClass="select" value={this.props.category} onChange={this.handleChange} >
          <option value="uv-max">UV Max</option>
          <option value="uv-speed">UV Speed</option>
          <option value="nm-speed">NM Speed</option>
          <option value="nm100">NM100</option>
        </FormControl>
      </FormGroup>
    );
  }
}