import React, { Component } from 'react';
import PropTypes from 'prop-types';
import LmpFileSelector from "./LmpFileSelector";
import { Button } from 'react-bootstrap';

export default class LmpUploadForm extends Component {

  static propTypes = {
    lmpAnalyzeRequest: PropTypes.func,
    analysisResult: PropTypes.object
  };

  static defaultProps = {
    analysisResult: {}
  };

  constructor(props) {
    super(props);
    this.onFormSubmit = this.onFormSubmit.bind(this);
  }

  onFormSubmit(e) {
    e.preventDefault();
  }

  render() {
    const { lmpAnalyzeRequest, analysisResult } = this.props;

    return (
      <form onSubmit={this.onFormSubmit}>
        <LmpFileSelector lmpAnalyzeRequest={lmpAnalyzeRequest} />
        <Button type="submit">Submit</Button>
      </form>
    );
  }
}