import React, {Component} from 'react';
import {observer} from 'mobx-react';
import SegmentTable from './SegmentTable';
import MachineSegmentTable from './MachineSegmentTable';
import MachineSegmentChart from './MachineSegmentChart';
@observer
class SegmentManagementTab extends Component {
    render() {
        return (
            <div className="segmentManagementTab">
                <SegmentTable store={this.props.store.segment}/>
                <MachineSegmentTable store={this.props.store.segment}/>
                <MachineSegmentChart store={this.props.store.segment}/>
            </div>
        );
    }
}
export default SegmentManagementTab;