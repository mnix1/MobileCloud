import React from 'react';
import {render} from 'react-dom';
import {
    BrowserRouter as Router,
    Route,
    Link
} from 'react-router-dom';
import FileManagementTab from './file/FileManagementTab';
import MachineManagementTab from './machine/MachineManagementTab';
import SegmentManagementTab from './segment/SegmentManagementTab';
import GlobalStore from './GlobalStore';
const store = new GlobalStore();
console.log(store);
render((
    <Router>
        <div>
            <ul className="navigationMenu">
                <li><Link to="/">File Management</Link></li>
                <li><Link to="/machine">Machine Management</Link></li>
                <li><Link to="/segment">Segment Management</Link></li>
            </ul>

            <hr/>

            <Route exact path="/" render={() => <FileManagementTab store={store}/>}/>
            <Route path="/machine" render={() => <MachineManagementTab store={store}/>}/>
            <Route path="/segment" render={() => <SegmentManagementTab store={store}/>}/>
        </div>
    </Router>
), document.getElementById('root'));