import React, {Component} from "react";
import UploadPanel from '../upload/UploadPanel';
import FileTable from './FileTable';
import CountPanel from '../count/CountPanel';
import OptionPanel from '../option/OptionPanel';
class FileManagementTab extends Component {
    handleClick() {
        $.ajax({
            url: '/balance/start',
        });
    }

    render() {
        return (
            <div className="fileManagementTab">
                <OptionPanel store={this.props.store.option}/>
                <button className="btn btn-warning balanceButton" onClick={this.handleClick}>Balance Cloud</button>
                <div className="uploadRecordingRow">
                    <UploadPanel store={this.props.store.option}/>
                    <CountPanel store={this.props.store.count}/>
                </div>
                <FileTable store={this.props.store.file}/>
            </div>
        );
    }
}
export default FileManagementTab;