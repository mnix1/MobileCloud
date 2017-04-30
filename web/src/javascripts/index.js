import React from 'react';
import {render} from 'react-dom';
import {
    BrowserRouter as Router,
    Route,
    Link
} from 'react-router-dom';
import FileManagementTab from './FileManagementTab';
import MachineManagementTab from './MachineManagementTab';
import GlobalStore from './GlobalStore';
const store = new GlobalStore();
console.log(store);
render((
    <Router>
        <div>
            <ul className="navigationMenu">
                <li><Link to="/">File Management</Link></li>
                <li><Link to="/machine">Machine Management</Link></li>
            </ul>

            <hr/>

            <Route exact path="/" render={() => <FileManagementTab store={store}/>}/>
            <Route path="/machine" component={MachineManagementTab}/>
        </div>
    </Router>
), document.getElementById('root'));