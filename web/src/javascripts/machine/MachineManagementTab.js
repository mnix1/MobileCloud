import React, {Component} from 'react';
import {observer} from 'mobx-react';
import MachineTable from './MachineTable';
@observer
class MachineManagementTab extends Component {
    constructor() {
        super();
    }

    render() {
        console.log(this.props);
        return (
            <div className="machineManagementTab">
                <MachineTable store={this.props.store.machine}/>
            </div>
        );
    }
}
export default MachineManagementTab;