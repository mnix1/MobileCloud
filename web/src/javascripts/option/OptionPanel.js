import React, {Component} from "react";
import OptionModal from "./OptionModal";
import {observer} from 'mobx-react';
@observer
class OptionPanel extends Component {
    render() {
        return (
            <div className="optionPanel">
                <OptionModal store={this.props.store}/>
            </div>
        );
    }
}
export default OptionPanel;