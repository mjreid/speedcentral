import React, {Component} from "react";
import DevTools from "./DevTools"

export class DevToolsWrapper extends Component {
  render() {
    if (process.env.NODE_ENV === 'production') {
      return (<div />);
    } else {
      return (<DevTools />);
    }
  }
}