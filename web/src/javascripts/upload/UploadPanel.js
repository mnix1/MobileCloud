import React, {Component} from "react";
import Gallery from 'react-fine-uploader/gallery'
import 'react-fine-uploader/gallery/gallery.css'
const statusTextOverride = {
  deleting: 'Usuwanie...',
  paused: 'Wstrzymano',
  queued: 'W kolejce',
  retrying_upload: 'Ponowna próba...',
  submitting: 'Potwierdzanie...',
  uploading: 'Przesyłanie...',
  upload_failed: 'Niepowodzenie',
  upload_successful: 'Zakończony'
};
const fileInputChildren = <span className="react-fine-uploader-gallery-file-input-content"><span><svg
  fill="#000000" height="24" viewBox="0 0 24 24" width="24"
  className="react-fine-uploader-gallery-file-input-upload-icon"><path d="M0 0h24v24H0z"
                                                                       fill="none"></path><path
  d="M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"></path></svg> Wybierz plik</span></span>

const dropzoneContent = <span className="react-fine-uploader-gallery-dropzone-content"><svg fill="#000000"
                                                                                            height="24"
                                                                                            viewBox="0 0 24 24"
                                                                                            width="24"
                                                                                            className="react-fine-uploader-gallery-dropzone-upload-icon"><path
  d="M0 0h24v24H0z" fill="none"></path><path
  d="M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"></path></svg> Przeciągnij plik tutaj</span>


class UploadPanel extends Component {
  constructor() {
    super();
    this.handleComplete = this.handleComplete.bind(this);
  }

  componentDidMount() {
    this.props.uploader.on('complete', this.handleComplete);
  }

  componentWillUnmount() {
    this.props.uploader.off('complete', this.handleComplete);
  }

  handleComplete(id, name, responseJSON, xhr) {
    this.props.onUploaded(this.props.uploader.methods.getUuid(id), this.props.uploader.methods.getName(id));
    if (id > 0 && this.gallery) {
      this.gallery._removeVisibleFile(id - 1)
    }
  }


  render() {
    return <div className="uploadPanel">
      <Gallery uploader={ this.props.uploader }
               ref={e => this.gallery = e}
               fileInput-children={ fileInputChildren }
               fileInput-multiple={false}
               dropzone-content={dropzoneContent}
               dropzone-multiple={false}
               validation={{
                 allowedExtensions: ['wav'],
                 itemLimit: 1
               }}
               status-text={statusTextOverride}/></div>
  }
}
export default UploadPanel;