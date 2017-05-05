import React, {Component} from "react";
import {observer} from 'mobx-react';
@observer
class CountPanel extends Component {
    constructor() {
        super();
    }

    render() {
        const store = this.props.store;
        return (
            <div className="countPanel">
                <div className="countPanelInputs">
                    <label>File id: <input onChange={e => store.fileIdentifier = e.target.value}
                                           placeholder="File identifier"
                                           value={store.fileIdentifier}/></label>
                    <label>Search: <input onChange={e => store.string = e.target.value} placeholder="Type word"
                                         value={store.string}/></label>
                </div>
                <button onClick={ store.update } className="btn btn-success countButton">Count</button>
                <div className="countResult">{store.description}</div>
            </div>
        );
    }
}
export default CountPanel;