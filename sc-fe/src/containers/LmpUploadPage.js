import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { lmpAnalyzeRequest } from '../modules/lmpanalyze';
import LmpUploadForm from "../components/lmp/LmpUploadForm";
import LmpFileSelector from "../components/lmp/LmpFileSelector";

class LmpUploadPage extends Component {

  static propTypes = {
    analysisResult: PropTypes.shape({
      map: PropTypes.number,
      pwads: PropTypes.array,
      skillLevel: PropTypes.number,
      iwad: PropTypes.string,
      episode: PropTypes.number,
      engineVersion: PropTypes.string
    })
  };

  static defaultProps = {
    analysisResult: {}
  };

  render() {
    const { analysisResult, lmpAnalyzeRequest } = this.props;

    if (Object.keys(analysisResult).length === 0) {
      return (
        <LmpFileSelector lmpAnalyzeRequest={lmpAnalyzeRequest} />
      );
    } else {
      return (
        <LmpUploadForm lmpAnalyzeRequest={lmpAnalyzeRequest} analysisResult={analysisResult} />
      );
    }
  }
}

function mapStateToProps(state) {
  return {
    analysisResult: state.lmpAnalysis.analysisResult
  };
}

function mapDispatchToProps(dispatch) {
  return({
    lmpAnalyzeRequest: (lmp) => dispatch(lmpAnalyzeRequest(lmp))
  });
}

export default connect(mapStateToProps, mapDispatchToProps)(LmpUploadPage);