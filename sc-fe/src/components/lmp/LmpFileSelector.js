import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormControl, FormGroup, ControlLabel, Col, Row} from 'react-bootstrap';
import './Lmp.css'

export default class LmpFileSelector extends Component {

  static propTypes = {
    lmpAnalyzeRequest: PropTypes.func.isRequired,
    file: PropTypes.object
  };

  constructor(props) {
    super(props);
    this.state = {
      file: null
    };
    this.onChange = this.onChange.bind(this)
  }

  onChange(e) {
    this.setState({ file: e.target.files[0] });

    let formData = new FormData();
    formData.append('lmp', e.target.files[0]);

    this.props.lmpAnalyzeRequest(formData);
  }

  render() {
    return (
      <FormGroup controlId="lmpFileSelector" className="LmpUploadControl BgOne">
        <Col componentClass={ControlLabel} sm={2}>
          Select LMP
        </Col>
        <Col sm={10}>
          <FormControl id="lmpFileSelector" type="file" onChange={this.onChange} />
        </Col>
      </FormGroup>
    );
  }
}