import {Component} from "react";
import {LinkContainer} from "react-router-bootstrap";
import {Button} from "react-bootstrap";

export class TopNavBar extends Component {
  render() {
    return (
      <div>
        <LinkContainer to="/search">
          <Button>Search</Button>
        </LinkContainer>
      </div>
    );
  }
}