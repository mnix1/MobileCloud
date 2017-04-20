import React, {Component} from 'react';
import {observer} from 'mobx-react';
@observer
class Management extends Component {
    constructor() {
        super();
    }

    render() {
        console.log(this.props);
        return (
            <div className="ELO">
                SIEMA
            </div>
        );
    }
}
export default Management;