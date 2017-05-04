import React, {Component} from "react";
import 'react-fine-uploader/gallery/gallery.css';
import {observer} from "mobx-react";
@observer
class UploadPanel extends Component {
    constructor() {
        super();
        this.handleComplete = this.handleComplete.bind(this);
    }

    handleComplete(id, name, responseJSON, xhr) {
        if (id > 0 && this.props.store.galleryRef) {
            this.props.store.galleryRef._removeVisibleFile(id - 1)
        }
    }

    render() {
        if (this.props.store.uploader) {
            this.props.store.uploader.off('complete', this.handleComplete);
        }
        this.props.store.uploader.on('complete', this.handleComplete);
        return <div className="uploadPanel">
            {this.props.store.gallery}
        </div>
    }
}
export default UploadPanel;