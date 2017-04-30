import React, {Component} from 'react';
import {observer} from 'mobx-react';
@observer
class MachineManagementTab extends Component {
    constructor() {
        super();
    }

    render() {
        console.log(this.props);
        return (
            <div>
                MachineManagementTab2
            </div>
        );
    }
}
export default MachineManagementTab;