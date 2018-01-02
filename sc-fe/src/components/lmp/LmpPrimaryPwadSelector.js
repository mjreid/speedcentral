import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';
import "./Lmp.css"

export default class LmpPrimaryPwadSelector extends Component {
  static propTypes = {
    iwad: PropTypes.string,
    pwad: PropTypes.shape({
      pwadFilename: PropTypes.string,
      pwadIdgamesLocation: PropTypes.string,
      pwadFriendlyName: PropTypes.string
    }),
    onLmpDataChanged: PropTypes.func,
    pwadResolveRequest: PropTypes.func
  };

  constructor(props) {
    super(props);
    this.handleFilenameChange = this.handleFilenameChange.bind(this);
    this.handleUrlChange = this.handleUrlChange.bind(this);
    this.resolvePwad = this.resolvePwad.bind(this);
  }

  handleFilenameChange(e) {
    const updatedFilename = e.target.value;
    const updatedPwad = Object.assign({}, this.props.pwad, { pwadFilename: updatedFilename });
    this.props.onLmpDataChanged({
      primaryPwad: updatedPwad
    });
    this.props.onLmpDataChanged(updatedPwad);
  }

  handleUrlChange(e) {
    const updatedUrl = e.target.value;
    const updatedPwad = Object.assign({}, this.props.pwad, { pwadIdgamesLocation: updatedUrl });
    this.props.onLmpDataChanged({
      primaryPwad: updatedPwad
    });
    this.props.onLmpDataChanged(updatedPwad);
  }

  resolvePwad(e) {
    const pwadFilename = e.target.value;
    const { pwadResolveRequest, iwad } = this.props;
    pwadResolveRequest(pwadFilename, iwad);
  }

  render() {
    if (!this.props.pwad) {
      return (<div />);
    }
    return (
      <FormGroup bsSize="sm" controlId="primaryPwadSelector" className="LmpUploadControl BgTwo">
        <Col sm={2}>
          Primary PWAD
        </Col>
        <Col sm={3}>
          <FormControl type="text" value={this.props.pwad.pwadFilename} onBlur={this.resolvePwad} onChange={this.handleFilenameChange} />
        </Col>
        <Col sm={7}>
          <FormControl type="text" value={this.props.pwad.pwadIdgamesLocation} onChange={this.handleUrlChange} />
        </Col>
      </FormGroup>
    )
  }
}