import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { FormGroup, ControlLabel, FormControl } from 'react-bootstrap';


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
          <FormGroup controlId="episode">
            <ControlLabel>Episode</ControlLabel>
            <FormControl id="episode" type="text" value={this.props.episode} onChange={this.handleEpisodeChange} />
          </FormGroup>
          <FormGroup controlId="map">
            <ControlLabel>Map</ControlLabel>
            <FormControl id="map" type="text" value={this.props.map} onChange={this.handleMapChange} />
          </FormGroup>
        </div>
      )
    } else {
      return (
        <div>
          <FormGroup controlId="map">
            <ControlLabel>Map</ControlLabel>
            <FormControl id="map" type="text" value={this.props.map} onChange={this.handleMapChange} />
          </FormGroup>
        </div>
      );
    }
  }
}