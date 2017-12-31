import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import {lmpAnalyzeDataChanged, lmpAnalyzeRequest} from '../modules/lmpanalyze';
import LmpUploadForm from "../components/lmp/LmpUploadForm";
import LmpFileSelector from "../components/lmp/LmpFileSelector";

class LmpUploadPage extends Component {

  static propTypes = {
    lmpAnalyzeDataChanged: PropTypes.func,
    analysisResult: PropTypes.shape({
      map: PropTypes.number,
      pwads: PropTypes.array,
      skillLevel: PropTypes.number,
      iwad: PropTypes.string,
      episode: PropTypes.number,
      engineVersion: PropTypes.string,
      // The following stuff is *not* determined by the analysis
      runner: PropTypes.string,
      submitter: PropTypes.string,
      category: PropTypes.string,
      runTime: PropTypes.number
    })
    /*lmpData: PropTypes.shape({
      map: PropTypes.number,
      episode: PropTypes.number,
      skillLevel: PropTypes.string,
      category: PropTypes.string,
      iwad: PropTypes.string,
      pwads: PropTypes.array,
      engineVersion: PropTypes.string,
      runner: PropTypes.string,
      submitter: PropTypes.string
    })*/
  };

  static defaultProps = {
    analysisResult: {},
  };

  render() {
    const { analysisResult, lmpAnalyzeRequest, lmpAnalyzeDataChanged } = this.props;

    if (Object.keys(analysisResult).length === 0) {
      return (
        <LmpFileSelector lmpAnalyzeRequest={lmpAnalyzeRequest} />
      );
    } else {
      return (
        <LmpUploadForm lmpAnalyzeRequest={lmpAnalyzeRequest} lmpData={analysisResult} onLmpDataChanged={lmpAnalyzeDataChanged} />
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
    lmpAnalyzeRequest: (lmp) => dispatch(lmpAnalyzeRequest(lmp)),
    lmpAnalyzeDataChanged: (updatedFields) => dispatch(lmpAnalyzeDataChanged(updatedFields))
  });
}

export default connect(mapStateToProps, mapDispatchToProps)(LmpUploadPage);