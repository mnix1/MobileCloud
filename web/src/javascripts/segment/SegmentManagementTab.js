import React, {Component} from 'react';
import {observer} from 'mobx-react';
import SegmentTable from './SegmentTable';
@observer
class SegmentManagementTab extends Component {
    constructor() {
        super();
    }

    render() {
        console.log(this.props);
        return (
            <div className="segmentManagementTab">
                <SegmentTable store={this.props.store.segment}/>
            </div>
        );
    }
}
export default SegmentManagementTab;