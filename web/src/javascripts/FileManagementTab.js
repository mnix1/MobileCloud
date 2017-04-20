import React, {Component} from "react";
import UploadPanel from './upload/UploadPanel';
import Uploader from './upload/Uploader';
class FileManagementTab extends Component {
  constructor() {
    super();
    this.uploader = new Uploader();
  }

  handleUploaded() {
  }

  render() {
    return (
      <div className="search">
        <div className="uploadRecordingRow">
          <UploadPanel uploader={this.uploader} onUploaded={this.handleUploaded.bind(this)}/>
        </div>
      </div>
    );
  }
}
export default FileManagementTab;