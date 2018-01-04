import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {Button, Col, Form, Row} from 'react-bootstrap';
import LmpIwadSelector from "./LmpIwadSelector";
import LmpMapSelector from "./LmpMapSelector";
import LmpCategorySelector from "./LmpCategorySelector";
import LmpPrimaryPwadSelector from "./LmpPrimaryPwadSelector";
import LmpResourcePwadSelector from "./LmpResourcePwadSelector";

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
    formData.append('runner', lmpData.runner);
    formData.append('submitter', lmpData.submitter);
    formData.append('category', lmpData.category);
    formData.append('primaryPwad', JSON.stringify(lmpData.primaryPwad));
    formData.append('secondaryPwads', JSON.stringify(lmpData.secondaryPwads));
    // formData.append('runTime', lmpData.runTime);
    // Temporary, since run time is required
    formData.append('runTime', "PT8M22S");

    submitDemoRequest(formData);
  }
//<LmpMapSelector episode={lmpData.episode} map={lmpData.map} onLmpDataChanged={onLmpDataChanged} />
  render() {
    const { lmpData, onLmpDataChanged, pwadResolveRequest } = this.props;

    return (
      <Form horizontal onSubmit={this.onFormSubmit}>
        <LmpIwadSelector iwad={lmpData.iwad} onLmpDataChanged={onLmpDataChanged} />
        <LmpMapSelector iwad={lmpData.iwad} onLmpDataChanged={onLmpDataChanged} episode={lmpData.episode} map={lmpData.map} />
        <LmpCategorySelector category={lmpData.category} onLmpDataChanged={onLmpDataChanged} />
        <LmpPrimaryPwadSelector pwadResolveRequest={pwadResolveRequest} onLmpDataChanged={onLmpDataChanged} iwad={lmpData.iwad} pwad={lmpData.primaryPwad} />
        <LmpResourcePwadSelector pwads={lmpData.secondaryPwads} onLmpDataChanged={onLmpDataChanged} iwad={lmpData.iwad} pwadResolveRequest={pwadResolveRequest} />
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