import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {Button, Col, Form, Row} from 'react-bootstrap';
import LmpIwadSelector from "./LmpIwadSelector";
import LmpMapSelector from "./LmpMapSelector";
import LmpCategorySelector from "./LmpCategorySelector";
import LmpPrimaryPwadSelector from "./LmpPrimaryPwadSelector";
import LmpResourcePwadSelector from "./LmpResourcePwadSelector";
import LmpRunner from "./LmpRunner";
import LmpSubmitter from "./LmpSubmitter";
import LmpTitlePreview from "./LmpTitlePreview";
import LmpRuntimeSelector from "./LmpRuntimeSelector";
import moment from "moment";

export default class LmpUploadForm extends Component {

  static propTypes = {
    lmpData: PropTypes.object,
    onLmpDataChanged: PropTypes.func,
    submitDemoRequest: PropTypes.func,
    pwadResolveRequest: PropTypes.func
  };

  static defaultProps = {
    lmpData: {}
  };

  groupOneClass = "BgOne";
  groupTwoClass = "BgTwo";

  constructor(props) {
    super(props);
    this.onFormSubmit = this.onFormSubmit.bind(this);
  }

  onFormSubmit(e) {
    e.preventDefault();
    const { lmpData, submitDemoRequest } = this.props;
    const formData = new FormData();
    // lmpData.lmp is the FormData object we used for submitting the analysis
    formData.append('lmp', lmpData.lmp.get("lmp"), "submission.lmp");
    formData.append('iwad', lmpData.iwad);
    formData.append('map', lmpData.map);
    formData.append('skillLevel', lmpData.skillLevel);
    formData.append('engineVersion', lmpData.engineVersion);
    formData.append('episode', lmpData.episode);

    if (lmpData.runner) {
      formData.append('runner', lmpData.runner);
    }
    if (lmpData.submitter) {
      formData.append('submitter', lmpData.submitter);
    }

    formData.append('category', lmpData.category);
    formData.append('primaryPwad', JSON.stringify(lmpData.primaryPwad));
    formData.append('secondaryPwads', JSON.stringify(lmpData.secondaryPwads));

    if (lmpData.runTime) {
      formData.append('runTime', lmpData.runTime);
    }

    submitDemoRequest(formData);
  }

  bgIndex = 0;

  alternateBackground() {
    this.bgIndex = this.bgIndex + 1;
    if (this.bgIndex % 2 === 0) {
      return this.groupOneClass;
    } else {
      return this.groupTwoClass;
    }
  }

  controlBackground() {
    return "LmpUploadControl " + this.alternateBackground();
  }

  render() {
    const { lmpData, onLmpDataChanged, pwadResolveRequest } = this.props;
    let secondaryPwads = (<div/>);
    if (lmpData.secondaryPwads && lmpData.secondaryPwads.length > 0) {
      secondaryPwads = (<LmpResourcePwadSelector pwads={lmpData.secondaryPwads} onLmpDataChanged={onLmpDataChanged} iwad={lmpData.iwad} pwadResolveRequest={pwadResolveRequest}
                                                 groupClass={this.controlBackground()} labelSize={2} filenameSize={3} urlSize={7}
      />);
    }

    return (
      <Form horizontal onSubmit={this.onFormSubmit}>
        <LmpTitlePreview lmpData={lmpData} className={this.controlBackground()} />
        <LmpIwadSelector iwad={lmpData.iwad} onLmpDataChanged={onLmpDataChanged}
                         groupClass={this.controlBackground()} labelSize={2} inputSize={10}
          />
        <LmpMapSelector iwad={lmpData.iwad} onLmpDataChanged={onLmpDataChanged} episode={lmpData.episode} map={lmpData.map}
                        groupClass={this.controlBackground()} mapLabelSize={2} mapInputSize={10} episodeLabelSize={1} episodeInputSize={3}
        />
        <LmpCategorySelector category={lmpData.category} onLmpDataChanged={onLmpDataChanged}
                             groupClass={this.controlBackground()} labelSize={2} inputSize={10}
          />
        <LmpRuntimeSelector category={lmpData.category} onLmpDataChanged={onLmpDataChanged}
                            groupClass={this.controlBackground()} labelSize={2} inputSize={10}
          />
        <LmpPrimaryPwadSelector pwadResolveRequest={pwadResolveRequest} onLmpDataChanged={onLmpDataChanged} iwad={lmpData.iwad} pwad={lmpData.primaryPwad}
                                groupClass={this.controlBackground()} labelSize={2} filenameSize={3} urlSize={7}
          />
        {secondaryPwads}
        <LmpRunner onLmpDataChanged={onLmpDataChanged} runner={lmpData.runner}
                   groupClass={this.controlBackground()} labelSize={2} inputSize={10}
          />
        <LmpSubmitter onLmpDataChanged={onLmpDataChanged} runner={lmpData.submitter}
                   groupClass={this.controlBackground()} labelSize={2} inputSize={10}
          />

        <Row className="BgOne">
          <Col xs={8} />
          <Col xs={4}>
            <Button type="submit">Submit</Button>
          </Col>
        </Row>

      </Form>
    );
  }
}