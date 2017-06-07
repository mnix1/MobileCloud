import React, {Component} from "react";
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';
import 'react-bootstrap-table/dist/react-bootstrap-table-all.min.css';
import ActionFormatter from "./ActionFormatter";
import {observer} from 'mobx-react';
@observer
class FileTable extends Component {
    constructor() {
        super();
    }

    render() {
        const actionFormatter = new ActionFormatter();
        // console.log('FileTable', this.props);
        return (
            <div className="panel panel-primary fileTable">
                <div className="panel-heading">
                    <h3 className="panel-title">Files</h3>
                </div>
                <div className="panel-body">
                    <BootstrapTable data={this.props.store.data} striped={true} hover={true}>
                        <TableHeaderColumn dataField="id" isKey={true} dataAlign="center"
                                           hidden={true}>Id</TableHeaderColumn>
                        <TableHeaderColumn dataField="identifier">Identifier</TableHeaderColumn>
                        <TableHeaderColumn dataField="name" dataSort={true}>Name</TableHeaderColumn>
                        <TableHeaderColumn dataField="segments" dataSort={true} dataAlign="right">Segments</TableHeaderColumn>
                        <TableHeaderColumn dataField="size" dataSort={true} dataAlign="right">Size</TableHeaderColumn>
                        <TableHeaderColumn dataFormat={ actionFormatter } hiddenOnInsert>Action</TableHeaderColumn>
                    </BootstrapTable>
                </div>
            </div>
        );
    }
}
export default FileTable;