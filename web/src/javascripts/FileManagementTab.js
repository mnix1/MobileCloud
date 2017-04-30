import React, {Component} from "react";
import UploadPanel from './upload/UploadPanel';
import Uploader from './upload/Uploader';
import { BootstrapTable, TableHeaderColumn } from 'react-bootstrap-table';
import 'react-bootstrap-table/dist/react-bootstrap-table-all.min.css';
var products = [{
    id: 1,
    name: "Item name 1",
    price: 100
},{
    id: 2,
    name: "Item name 2",
    price: 100
}];
// It's a data format example.
function priceFormatter(cell, row){
    return '<i class="glyphicon glyphicon-usd"></i> ' + cell;
}
class FileManagementTab extends Component {
    constructor() {
        super();
        this.handleUploaded = this.handleUploaded.bind(this);
        this.uploader = new Uploader();
        // this.uploader2 = new Uploader({
        //     options: {
        //         chunking: {
        //             enabled: true,
        //             success: {endpoint: '/segment/uploadSuccess'},
        //             concurrent: {
        //                 enabled: true
        //             },
        //             partSize: 1024 * 1024
        //         },
        //         request: {
        //             endpoint: '/segment/upload'
        //         },
        //         retry: {
        //             enableAuto: true
        //         }
        //     }
        // });
    }

    handleUploaded() {
    }

    render() {
        return (
            <div className="fileManagementTab">
                <div className="uploadRecordingRow">
                    <UploadPanel uploader={this.uploader} onUploaded={this.handleUploaded}/>
                </div>
                <div className="files">
                    <BootstrapTable data={products} striped={true} hover={true}>
                        <TableHeaderColumn dataField="id" isKey={true} dataAlign="center" dataSort={true}>Product ID</TableHeaderColumn>
                        <TableHeaderColumn dataField="name" dataSort={true}>Product Name</TableHeaderColumn>
                        <TableHeaderColumn dataField="price" dataFormat={priceFormatter}>Product Price</TableHeaderColumn>
                    </BootstrapTable>,
                </div>
            </div>
        );
    }
}
export default FileManagementTab;