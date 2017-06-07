import React, {Component} from "react";
import {BootstrapTable, TableHeaderColumn} from "react-bootstrap-table";
import "react-bootstrap-table/dist/react-bootstrap-table-all.min.css";
import ActionFormatter from "./ActionFormatter";
import {observer} from "mobx-react";
@observer
class Header extends Component {
    render() {
        return <div className="tableHeader">
            {this.props.children}
            <br/>
            <input onChange={e => this.props.store.handleSearch(this.props.id, e.target.value)}
                   value={this.props.store.search[this.props.id]} placeholder="Search"/>
        </div>
    }
}

@observer
class SegmentTable extends Component {
    render() {
        const options = {
            sizePerPage: 10
        };
        const actionFormatter = new ActionFormatter();
        // console.log('SegmentTable', this.props);
        return (
            <div className="panel panel-primary segmentTable">
                <div className="panel-heading">
                    <h3 className="panel-title">Segments</h3>
                </div>
                <div className="panel-body">
                    <BootstrapTable data={this.props.store.rows} options={ options } striped={true} hover={true}
                                    pagination={true}>
                        <TableHeaderColumn dataField="id" isKey={true} dataAlign="center"
                                           hidden={true}>Id</TableHeaderColumn>
                        <TableHeaderColumn dataField="identifier"><Header store={this.props.store}
                                                                          id="identifier">Identifier</Header></TableHeaderColumn>
                        <TableHeaderColumn dataField="fileIdentifier"><Header store={this.props.store}
                                                                              id="fileIdentifier">File
                            Identifier</Header></TableHeaderColumn>
                        <TableHeaderColumn dataField="machineIdentifier" dataAlign="right"><Header
                            store={this.props.store}
                            id="machineIdentifier">Machine
                            Identifier</Header></TableHeaderColumn>
                        <TableHeaderColumn dataField="byteFrom" dataSort={true} dataAlign="right">Byte
                            From</TableHeaderColumn>
                        <TableHeaderColumn dataField="byteTo" dataSort={true} dataAlign="right">Byte
                            To</TableHeaderColumn>
                        <TableHeaderColumn dataFormat={ actionFormatter } hiddenOnInsert>Action</TableHeaderColumn>
                    </BootstrapTable>
                </div>
            </div>
        );
    }
}
export default SegmentTable;