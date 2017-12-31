import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { FormGroup, ControlLabel, FormControl } from 'react-bootstrap';


export default class LmpRunner extends Component {

  static propTypes = {
    runner: PropTypes.string,
    onLmpDataChanged: PropTypes.func,
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
        <FormGroup controlId="runner">
          <ControlLabel>Runner</ControlLabel>
          <FormControl id="runner" type="text" value={this.props.runner} onChange={this.handleChange} />
        </FormGroup>
      </div>
    );
  }
}