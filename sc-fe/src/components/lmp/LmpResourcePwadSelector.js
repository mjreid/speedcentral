import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';
import "./Lmp.css"

export default class LmpResourcePwadSelector extends Component {
  static propTypes = {
    iwad: PropTypes.string,
    pwads: PropTypes.arrayOf(PropTypes.shape({
      pwadFilename: PropTypes.string,
      pwadIdgamesLocation: PropTypes.string
    })),
    onLmpDataChanged: PropTypes.func,
    pwadResolveRequest: PropTypes.func,
    groupClass: PropTypes.string.required,
    labelSize: PropTypes.number.required,
    filenameSize: PropTypes.number.required,
    urlSize: PropTypes.number.required,
  };

  constructor(props) {
    super(props);
    this.onPwadFilenameBlur = this.onPwadFilenameBlur.bind(this);
    this.onPwadUrlChange = this.onPwadUrlChange.bind(this);
    this.onPwadFilenameChange = this.onPwadFilenameChange.bind(this);
  }

  onPwadFilenameChange(pwad, event) {
    const { pwads } = this.props;
    const updatedFilename = event.target.value;
    const pwadIndex = pwads.findIndex(i => i.pwadFilename === pwad.pwadFilename);
    const updatedPwad = Object.assign({}, pwad, { pwadFilename: updatedFilename });
    let updatedPwadList = pwads;
    updatedPwadList[pwadIndex] = updatedPwad;
    this.props.onLmpDataChanged({
      secondaryPwads: updatedPwadList
    });
  }

  onPwadUrlChange(pwad, event) {
    const { pwads } = this.props;
    const updatedFilename = event.target.value;
    const pwadIndex = pwads.findIndex(i => i.pwadFilename === pwad.pwadFilename);
    const updatedPwad = Object.assign({}, pwad, { pwadFilename: updatedFilename });
    let updatedPwadList = pwads;
    updatedPwadList[pwadIndex] = updatedPwad;
    this.props.onLmpDataChanged({
      secondaryPwads: updatedPwadList
    });
  }

  onPwadFilenameBlur(e) {
    const pwadFilename = e.target.value;
    const { pwadResolveRequest, iwad } = this.props;
    pwadResolveRequest(pwadFilename, iwad);
  }

  render() {
    const { pwads } = this.props;
    const onPwadFilenameBlur = this.onPwadFilenameBlur;
    const onPwadFilenameChange = this.onPwadFilenameChange;
    const onPwadUrlChange = this.onPwadUrlChange;
    if (!pwads || pwads.length === 0) {
      return (<div />);
    }

    const { groupClass, labelSize, filenameSize, urlSize } = this.props;
    const secondaryPwads = pwads.map(function(pwad) {
      const selectorId = `resourcePwadSelector${pwad}`;
      return (
        <FormGroup bsSize="sm" controlId={selectorId} className={groupClass}>
          <Col sm={labelSize} />
          <Col sm={filenameSize}>
            <FormControl type="text" value={pwad.pwadFilename} onBlur={onPwadFilenameBlur} onChange={(e) => onPwadFilenameChange(pwad, e)} />
          </Col>
          <Col sm={urlSize}>
            <FormControl type="text" value={pwad.pwadIdgamesLocation} onChange={onPwadUrlChange} />
          </Col>
        </FormGroup>
      );
    });

    return (
      <div>{secondaryPwads}</div>
    );
  }
}