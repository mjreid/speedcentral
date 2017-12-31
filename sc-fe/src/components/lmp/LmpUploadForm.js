import React, { Component } from 'react';
import PropTypes from 'prop-types';
import LmpFileSelector from "./LmpFileSelector";
import {Button, Form} from 'react-bootstrap';
import LmpIwadSelector from "./LmpIwadSelector";
import LmpMapSelector from "./LmpMapSelector";

export default class LmpUploadForm extends Component {

  static propTypes = {
    lmpAnalyzeRequest: PropTypes.func,
    lmpData: PropTypes.object,
    onLmpDataChanged: PropTypes.func
  };

  static defaultProps = {
    lmpData: {}
  };

  constructor(props) {
    super(props);
    this.onFormSubmit = this.onFormSubmit.bind(this);
  }

  onFormSubmit(e) {
    e.preventDefault();
  }
//<LmpMapSelector episode={lmpData.episode} map={lmpData.map} onLmpDataChanged={onLmpDataChanged} />
  render() {
    const { lmpAnalyzeRequest, lmpData, onLmpDataChanged } = this.props;

    return (
      <Form horizontal onSubmit={this.onFormSubmit}>
        <LmpFileSelector lmpAnalyzeRequest={lmpAnalyzeRequest} />
        <LmpIwadSelector iwad={lmpData.iwad} onLmpDataChanged={onLmpDataChanged} />
        <LmpMapSelector iwad={lmpData.iwad} onLmpDataChanged={onLmpDataChanged} episode={lmpData.episode} map={lmpData.map} />
        <Button type="submit">Submit</Button>
      </Form>
    );
  }
}