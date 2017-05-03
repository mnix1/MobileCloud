import React, {Component} from "react";
import Gallery from 'react-fine-uploader/gallery';
import 'react-fine-uploader/gallery/gallery.css';
import {observer} from "mobx-react";
@observer
class UploadPanel extends Component {
    constructor() {
        super();
        this.handleComplete = this.handleComplete.bind(this);
    }

    handleComplete(id, name, responseJSON, xhr) {
        if (id > 0 && this.gallery) {
            this.gallery._removeVisibleFile(id - 1)
        }
    }


    render() {
        if (this.props.store.uploader) {
            this.props.store.uploader.off('complete', this.handleComplete);
        }
        this.props.store.uploader.on('complete', this.handleComplete);
        return <div className="uploadPanel">
            <Gallery uploader={ this.props.store.uploader }
                     ref={e => this.gallery = e}
                     fileInput-multiple={false}
                     dropzone-multiple={false}
                     validation={{
                         allowedExtensions: ['wav'],
                         itemLimit: 1
                     }}
            />
        </div>
    }
}
export default UploadPanel;