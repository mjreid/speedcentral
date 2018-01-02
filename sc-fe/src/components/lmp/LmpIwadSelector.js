import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';


export default class LmpIwadSelector extends Component {

  static propTypes = {
    iwad: PropTypes.string,
    onLmpDataChanged: PropTypes.func,
  };


  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    const iwad = event.target.value;
    this.props.onLmpDataChanged({ iwad: iwad });
  }

  render() {
    // Adblock Plus blocks elements with id "iwad" (WTF?)
    return (
      <FormGroup controlId="doomIwad" className="LmpUploadControl BgTwo" bsSize="sm">
        <Col componentClass={ControlLabel} sm={2}>
          IWAD
        </Col>
        <Col sm={10}>
          <FormControl componentClass="select" placeholder="doom2" value={this.props.iwad} onChange={this.handleChange} >
            <option value="doom">Doom</option>
            <option value="doom2">Doom 2</option>
          </FormControl>
        </Col>
      </FormGroup>
    );
  }
}