import React, {Component} from "react";
import UploadPanel from './upload/UploadPanel';
import Uploader from './upload/Uploader';
class FileManagementTab extends Component {
    constructor() {
        super();
        this.uploader = new Uploader();
        this.uploader2 = new Uploader({
            options: {
                chunking: {
                    enabled: true,
                    success: {endpoint: '/segment/uploadSuccess'},
                    concurrent: {
                        enabled: true
                    },
                    partSize: 1024 * 1024
                },
                request: {
                    endpoint: '/segment/upload'
                },
                retry: {
                    enableAuto: true
                }
            }
        });
    }

    handleUploaded() {
    }

    render() {
        return (
            <div className="search">
                <div className="uploadRecordingRow">
                    <UploadPanel uploader={this.uploader} onUploaded={this.handleUploaded.bind(this)}/>
                    <UploadPanel uploader={this.uploader2} onUploaded={this.handleUploaded.bind(this)}/>
                </div>
            </div>
        );
    }
}
export default FileManagementTab;