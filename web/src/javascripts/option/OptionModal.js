import React, {Component} from "react";
import {Modal, ModalHeader, ModalTitle, ModalClose, ModalBody, ModalFooter} from "react-modal-bootstrap";
import {DropdownButton, MenuItem} from "react-bootstrap";
import "react-bootstrap-switch/dist/css/bootstrap3/react-bootstrap-switch.min.css";
import {ToastContainer, ToastMessage} from "react-toastr";
import {observer} from "mobx-react";
const ToastMessageFactory = React.createFactory(ToastMessage.animation);
@observer
class OptionModal extends Component {
    constructor(props) {
        super(props);
        this.openModal = this.openModal.bind(this);
        this.hideModal = this.hideModal.bind(this);
        this.handleSave = this.handleSave.bind(this);
        this.state = {
            isOpen: props.isOpen,
            segmentSize: props.store.segmentSize,
            uploadAlgorithm: props.store.uploadAlgorithm,
            speedFactor: props.store.speedFactor,
            balancedPreference: props.store.balancedPreference,
        };
    }

    openModal() {
        this.setState({
            isOpen: true,
            segmentSize: this.props.store.segmentSize,
            uploadAlgorithm: this.props.store.uploadAlgorithm,
            speedFactor: this.props.store.speedFactor,
            balancedPreference: this.props.store.balancedPreference,
        });
    }

    hideModal() {
        this.setState({
            isOpen: false
        });
    }

    handleSave() {
        $.ajax({
            url: '/option/set',
            data: {
                segmentSize: this.state.segmentSize,
                uploadAlgorithm: this.state.uploadAlgorithm,
                speedFactor: this.state.speedFactor,
                balancedPreference: this.state.balancedPreference,
            },
            type: 'POST',
            success: () => {
                this.props.store.update();
                this.refs.container.success('Options saved', `SUCCESS!`, {
                    closeButton: true,
                    timeOut: 2000,
                });
            }
        });
        this.hideModal();
    }

    render() {
        return (
            <div className="optionModal">
                <ToastContainer
                    toastMessageFactory={ToastMessageFactory}
                    ref="container"
                    className="toast-top-right"
                />
                <button className="btn btn-danger btn-xs" onClick={this.openModal}>Options</button>
                <Modal isOpen={this.state.isOpen} onRequestHide={this.hideModal}>
                    <ModalHeader>
                        <ModalClose onClick={this.hideModal}/>
                        <ModalTitle>Options</ModalTitle>
                    </ModalHeader>
                    {this.renderModalBody()}
                    <ModalFooter>
                        <button className='btn btn-default' onClick={this.hideModal}>
                            Close
                        </button>
                        <button onClick={this.handleSave} className='btn btn-primary'>
                            Save
                        </button>
                    </ModalFooter>
                </Modal>
            </div>
        );
    }

    renderModalBody() {
        return <ModalBody>
            <div className='form-group'>
                <label>Segment Size:</label>
                <DropdownButton bsStyle='default' title={this.state.segmentSize}
                                id="uploadAlgorithmDropdown">
                    {this.renderSegmentSizeItems()}
                </DropdownButton>
            </div>
            <div className='form-group'>
                <label>Upload algorithm:</label>
                <DropdownButton bsStyle='default' title={this.state.uploadAlgorithm}
                                id="uploadAlgorithmDropdown">
                    {this.renderUploadAlgorithmItems()}
                </DropdownButton>
            </div>
            <div className='form-group'>
                <label>Speed Factor:</label>
                <input onChange={e => {
                    this.setState({speedFactor: e.target.value})
                }} type="number" step="0.01" value={this.state.speedFactor}/>
            </div>
            <div className='form-group'>
                <label>Balance Preference:</label>
                <input onChange={e => {
                    this.setState({balancedPreference: e.target.value})
                }} type="number" step="0.01" value={this.state.balancedPreference}/>
            </div>
        </ModalBody>
    }

    renderSegmentSizeItems() {
        return this.props.store.segmentSizes.map((e, i) => {
            return <MenuItem onClick={ menuItem => this.setState({segmentSize: e})} eventKey={e} key={i}
                             active={e == this.state.segmentSize}>{e}</MenuItem>
        })
    }

    renderUploadAlgorithmItems() {
        return this.props.store.uploadAlgorithms.map((e, i) => {
            return <MenuItem onClick={ menuItem => this.setState({uploadAlgorithm: e})} eventKey={e} key={i}
                             active={e == this.state.uploadAlgorithm}>{e}</MenuItem>
        })
    }
}
export default OptionModal;