import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {FormGroup, ControlLabel, FormControl, Col} from 'react-bootstrap';
import "./Lmp.css"

export default class LmpMapSelector extends Component {

  static propTypes = {
    iwad: PropTypes.string,
    map: PropTypes.string,
    episode: PropTypes.string,
    onLmpDataChanged: PropTypes.func,
    groupClass: PropTypes.string.required,
    mapLabelSize: PropTypes.number.required,
    mapInputSize: PropTypes.number.required,
    episodeLabelSize: PropTypes.number.required,
    episodeInputSize: PropTypes.number.required,
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
          <FormGroup bsSize="sm" controlId="episode" className={this.props.groupClass}>
            <Col componentClass={ControlLabel} sm={this.props.episodeLabelSize}>
              Episode
            </Col>
            <Col sm={this.props.episodeInputSize}>
              <FormControl type="text" value={this.props.episode} onChange={this.handleEpisodeChange} />
            </Col>
            <Col componentClass={ControlLabel} sm={this.props.episodeLabelSize}>
              Map
            </Col>
            <Col sm={this.props.episodeInputSize}>
              <FormControl type="text" value={this.props.map} onChange={this.handleMapChange} />
            </Col>
          </FormGroup>
        </div>
      )
    } else {
      return (
        <FormGroup bsSize="sm" controlId="map" className={this.props.groupClass}>
          <Col componentClass={ControlLabel} sm={this.props.mapLabelSize}>
            Map
          </Col>
          <Col sm={this.props.mapInputSize}>
            <FormControl type="text" value={this.props.map} onChange={this.handleMapChange} />
          </Col>
        </FormGroup>
      );
    }
  }
}