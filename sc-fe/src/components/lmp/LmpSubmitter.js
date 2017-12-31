import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { FormGroup, ControlLabel, FormControl } from 'react-bootstrap';

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
      <div>
        <FormGroup controlId="submitter">
          <ControlLabel>Runner</ControlLabel>
          <FormControl id="submitter" type="text" value={this.props.submitter} onChange={this.handleChange} />
        </FormGroup>
      </div>
    );
  }
}