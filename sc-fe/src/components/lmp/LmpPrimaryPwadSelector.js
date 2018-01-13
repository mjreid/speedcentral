import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col, HelpBlock} from 'react-bootstrap';
import "./Lmp.css"

export default class LmpPrimaryPwadSelector extends Component {
  static propTypes = {
    iwad: PropTypes.string,
    pwad: PropTypes.shape({
      pwadFilename: PropTypes.string,
      pwadIdgamesLocation: PropTypes.string,
      pwadFriendlyName: PropTypes.string,
      errorMessage: PropTypes.string,
    }),
    onLmpDataChanged: PropTypes.func,
    pwadResolveRequest: PropTypes.func,
    groupClass: PropTypes.string.required,
    labelSize: PropTypes.number.required,
    filenameSize: PropTypes.number.required,
    urlSize: PropTypes.number.required,
  };

  constructor(props) {
    super(props);
    this.handleFilenameChange = this.handleFilenameChange.bind(this);
    this.handleUrlChange = this.handleUrlChange.bind(this);
    this.resolvePwad = this.resolvePwad.bind(this);
  }

  handleFilenameChange(e) {
    const updatedFilename = e.target.value.toLowerCase();
    const updatedPwad = Object.assign({}, this.props.pwad, { pwadFilename: updatedFilename });
    this.props.onLmpDataChanged({
      primaryPwad: updatedPwad
    });
  }

  handleUrlChange(e) {
    const updatedUrl = e.target.value.toLowerCase();
    const updatedPwad = Object.assign({}, this.props.pwad, { pwadIdgamesLocation: updatedUrl });
    this.props.onLmpDataChanged({
      primaryPwad: updatedPwad
    });
  }

  resolvePwad(e) {
    let pwadFilename = e.target.value;

    if (pwadFilename) {
      pwadFilename = pwadFilename.trim().toLowerCase();
      if (pwadFilename.endsWith(".wad") || pwadFilename.endsWith(".zip")) {
        pwadFilename = pwadFilename.substr(0, pwadFilename.length - 4);
      }
      const { pwadResolveRequest, iwad } = this.props;
      pwadResolveRequest(pwadFilename, iwad);
    } else {
      const updatedPwad = Object.assign({}, this.props.pwad, { pwadIdgamesLocation: "" });
      this.props.onLmpDataChanged({
        primaryPwad: updatedPwad
      });
    }
  }

  getValidationState() {
    if (this.props.pwad.errorMessage) {
      return "error";
    } else {
      return null;
    }
  }

  render() {

    const { pwadFilename, pwadIdgamesLocation, errorMessage } = this.props.pwad;

    return (
      <FormGroup bsSize="sm" controlId="primaryPwadSelector" className={this.props.groupClass} validationState={this.getValidationState()}>
        <Col sm={this.props.labelSize} componentClass={ControlLabel} >
          PWAD
        </Col>
        <Col sm={this.props.filenameSize}>
          <FormControl type="text" value={pwadFilename} onBlur={this.resolvePwad} onChange={this.handleFilenameChange} />
          {errorMessage && <HelpBlock>{errorMessage}</HelpBlock>}
        </Col>
        <Col sm={this.props.urlSize}>
          <FormControl type="text" value={pwadIdgamesLocation} onChange={this.handleUrlChange} disabled={true} />
        </Col>
      </FormGroup>
    )
  }
}