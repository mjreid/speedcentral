import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import {lmpAnalyzeDataChanged, lmpAnalyzeRequest} from '../modules/lmpanalyze';
import LmpUploadForm from "../components/lmp/LmpUploadForm";
import LmpFileSelector from "../components/lmp/LmpFileSelector";
import {demoSubmitRequest} from "../modules/demosubmit";

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
  };

  static defaultProps = {
    analysisResult: {},
  };

  render() {
    const { analysisResult, lmpAnalyzeRequest, lmpAnalyzeDataChanged, submitDemoRequest } = this.props;

    const lmpFileSelector = (
      <LmpFileSelector lmpAnalyzeRequest={lmpAnalyzeRequest} />
    );
    if (Object.keys(analysisResult).length === 0) {
      return lmpFileSelector;
    } else {
      return (
        <LmpUploadForm lmpAnalyzeRequest={lmpAnalyzeRequest} lmpData={analysisResult} onLmpDataChanged={lmpAnalyzeDataChanged} submitDemoRequest={submitDemoRequest}>
          {lmpFileSelector}
        </LmpUploadForm>
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
    lmpAnalyzeDataChanged: (updatedFields) => dispatch(lmpAnalyzeDataChanged(updatedFields)),
    submitDemoRequest: (lmpData) => dispatch(demoSubmitRequest(lmpData))
  });
}

export default connect(mapStateToProps, mapDispatchToProps)(LmpUploadPage);