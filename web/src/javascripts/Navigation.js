import React, {Component} from 'react';
import Management from './Management';
import {observer} from 'mobx-react';
@observer
class Navigation extends Component {
    constructor() {
        super();
    }

    render() {
        return (
            <div className="mainWrapper">
                <ul className="tabs" role="nav">
                    <li>Pliki</li>
                </ul>
                <div>
                    <Management store={this.props.store}/>
                </div>
            </div>
        );
    }
}
export default Navigation;