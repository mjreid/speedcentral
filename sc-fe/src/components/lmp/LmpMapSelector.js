import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';
import "./Lmp.css"

export default class LmpMapSelector extends Component {

  static propTypes = {
    iwad: PropTypes.string,
    map: PropTypes.number,
    episode: PropTypes.number,
    onLmpDataChanged: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleMapChange = this.handleMapChange.bind(this);
    this.handleEpisodeChange = this.handleEpisodeChange.bind(this);
  }

  handleMapChange(event) {
    if (!isNaN(event.target.value)) {
      const map = parseInt(event.target.value, 10);
      this.props.onLmpDataChanged({map: map});
    }
  }

  handleEpisodeChange(event) {
    if (!isNaN(event.target.value)) {
      const episode = parseInt(event.target.value, 10);
      this.props.onLmpDataChanged({episode: episode});
    }
  }

  render() {
    // Allow both episode and map if iwad is doom, otherwise it's just a map and episode is 1
    if (this.props.iwad === "doom") {
      return (
        <div>
          <FormGroup bsSize="sm" controlId="episode" className="LmpUploadControl BgOne">
            <Col componentClass={ControlLabel} sm={2}>
              Episode
            </Col>
            <Col sm={4}>
              <FormControl type="text" value={this.props.episode} onChange={this.handleEpisodeChange} />
            </Col>
            <Col componentClass={ControlLabel} sm={2}>
              Map
            </Col>
            <Col sm={4}>
              <FormControl type="text" value={this.props.map} onChange={this.handleMapChange} />
            </Col>
          </FormGroup>
        </div>
      )
    } else {
      return (
        <FormGroup bsSize="sm" controlId="map" className="LmpUploadControl BgOne">
          <Col componentClass={ControlLabel} sm={2}>
            Map
          </Col>
          <Col sm={10}>
            <FormControl type="text" value={this.props.map} onChange={this.handleMapChange} />
          </Col>
        </FormGroup>
      );
    }
  }
}