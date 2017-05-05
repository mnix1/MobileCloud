import React, {Component} from "react";
import {BootstrapTable, TableHeaderColumn} from "react-bootstrap-table";
import "react-bootstrap-table/dist/react-bootstrap-table-all.min.css";
import ActionFormatter from "./ActionFormatter";
import {observer} from "mobx-react";
@observer
class MachineTable extends Component {
    constructor() {
        super();
    }

    render() {
        const addressFormatter = (cell, row) => {
            const preparedAddress = 'http://' + row.ipAddress + ':8090';
            return <a href={row.ipAddress ? preparedAddress : ''}>{row.ipAddress}</a>;
        };
        const actionFormatter = new ActionFormatter();
        return (
            <div className="panel panel-primary machineTable">
                <div className="panel-heading">
                    <h3 className="panel-title">Machines</h3>
                </div>
                <div className="panel-body">
                    <BootstrapTable data={this.props.store.data} striped={true} hover={true}>
                        <TableHeaderColumn dataField="id" isKey={true} dataAlign="center"
                                           hidden={true}>Id</TableHeaderColumn>
                        <TableHeaderColumn dataField="identifier">Identifier</TableHeaderColumn>
                        <TableHeaderColumn dataField="name">Name</TableHeaderColumn>
                        <TableHeaderColumn dataField="device">Device</TableHeaderColumn>
                        <TableHeaderColumn dataField="system">System</TableHeaderColumn>
                        <TableHeaderColumn dataField="role" dataSort={true}>Role</TableHeaderColumn>
                        <TableHeaderColumn dataField="ipAddress" dataSort={true} dataAlign="right"
                                           dataFormat={addressFormatter}>Ip
                            Address</TableHeaderColumn>
                        <TableHeaderColumn dataField="lastContact" dataSort={true} dataAlign="right">Last
                            Contact</TableHeaderColumn>
                        <TableHeaderColumn dataField="speed" dataSort={true} dataAlign="right">Speed</TableHeaderColumn>
                        <TableHeaderColumn dataField="space" dataSort={true} dataAlign="right">Space</TableHeaderColumn>
                        <TableHeaderColumn dataFormat={ actionFormatter } hiddenOnInsert>Action</TableHeaderColumn>
                    </BootstrapTable>
                </div>
            </div>
        );
    }
}
export default MachineTable;