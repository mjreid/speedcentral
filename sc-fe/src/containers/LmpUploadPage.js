import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import {lmpAnalyzeDataChanged, lmpAnalyzeRequest, pwadResolveRequest, pwadResolveSuccess} from '../modules/lmpanalyze';
import LmpUploadForm from "../components/lmp/LmpUploadForm";
import LmpFileSelector from "../components/lmp/LmpFileSelector";
import {demoSubmitRequest} from "../modules/demosubmit";

class LmpUploadPage extends Component {

  static propTypes = {
    lmpAnalyzeRequest: PropTypes.func,
    lmpAnalyzeDataChanged: PropTypes.func,
    submitDemoRequest: PropTypes.func,
    pwadResolveRequest: PropTypes.func,
    analysisResult: PropTypes.shape({
      map: PropTypes.string,
      pwads: PropTypes.array,
      skillLevel: PropTypes.string,
      iwad: PropTypes.string,
      episode: PropTypes.string,
      engineVersion: PropTypes.string,
      // The following stuff is *not* determined by the analysis
      runner: PropTypes.string,
      submitter: PropTypes.string,
      category: PropTypes.string,
      runTime: PropTypes.number,
      primaryPwad: PropTypes.shape({
        pwadFilename: PropTypes.string,
        pwadIdgamesLocation: PropTypes.string
      }),
      secondaryPwads: PropTypes.arrayOf(PropTypes.shape({
        pwadFilename: PropTypes.string,
        pwadIdgamesLocation: PropTypes.string
      })),
    })
  };

  static defaultProps = {
    analysisResult: {},
  };

  render() {
    const { analysisResult, lmpAnalyzeRequest, lmpAnalyzeDataChanged, submitDemoRequest, pwadResolveRequest } = this.props;

    const lmpFileSelector = (
      <LmpFileSelector lmpAnalyzeRequest={lmpAnalyzeRequest} />
    );
    if (Object.keys(analysisResult).length === 0) {
      return lmpFileSelector;
    } else {
      return (
        <LmpUploadForm
          lmpAnalyzeRequest={lmpAnalyzeRequest}
          lmpData={analysisResult}
          onLmpDataChanged={lmpAnalyzeDataChanged}
          submitDemoRequest={submitDemoRequest}
          pwadResolveRequest={pwadResolveRequest}>
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
    submitDemoRequest: (lmpData) => dispatch(demoSubmitRequest(lmpData)),
    pwadResolveRequest: (pwadFilename, iwad) => dispatch(pwadResolveRequest(pwadFilename, iwad))
  });
}

export default connect(mapStateToProps, mapDispatchToProps)(LmpUploadPage);