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
            replicaSize: props.store.replicaSize,
            uploadAlgorithm: props.store.uploadAlgorithm,
            balanceAlgorithm: props.store.balanceAlgorithm,
            speedFactor: props.store.speedFactor,
            balancedPreference: props.store.balancedPreference,
            utilizationThreshold: props.store.utilizationThreshold,
            dhtModulo: props.store.dhtModulo,
        };
    }

    openModal() {
        this.setState({
            isOpen: true,
            segmentSize: this.props.store.segmentSize,
            replicaSize: this.props.store.replicaSize,
            uploadAlgorithm: this.props.store.uploadAlgorithm,
            balanceAlgorithm: this.props.store.balanceAlgorithm,
            speedFactor: this.props.store.speedFactor,
            balancedPreference: this.props.store.balancedPreference,
            utilizationThreshold: this.props.store.utilizationThreshold,
            dhtModulo: this.props.store.dhtModulo,
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
                replicaSize: this.state.replicaSize,
                uploadAlgorithm: this.state.uploadAlgorithm,
                balanceAlgorithm: this.state.balanceAlgorithm,
                speedFactor: this.state.speedFactor,
                balancedPreference: this.state.balancedPreference,
                utilizationThreshold: this.state.utilizationThreshold,
                dhtModulo: this.state.dhtModulo,
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
                <label>Number of Replica:</label>
                <input onChange={e => {
                    this.setState({replicaSize: e.target.value})
                }} type="number" min="0" step="1" value={this.state.replicaSize}/>
            </div>
            <div className='form-group'>
                <label>Upload Algorithm:</label>
                <DropdownButton bsStyle='default' title={this.state.uploadAlgorithm}
                                id="uploadAlgorithmDropdown">
                    {this.renderUploadAlgorithmItems()}
                </DropdownButton>
            </div>
            <hr/>
            <div className='form-group'>
                <label>Balance Algorithm:</label>
                <DropdownButton bsStyle='default' title={this.state.balanceAlgorithm}
                                id="balanceAlgorithmDropdown">
                    {this.renderBalanceAlgorithmItems()}
                </DropdownButton>
            </div>
            <hr/>
            <div className='form-group'>
                <label>HDFS Balance Preference:</label>
                <input onChange={e => {
                    this.setState({balancedPreference: e.target.value})
                }} type="number" step="0.01" min="0" max="1" value={this.state.balancedPreference}/>
            </div>
            <div className='form-group'>
                <label>Hadaps Speed Factor:</label>
                <input onChange={e => {
                    this.setState({speedFactor: e.target.value})
                }} type="number" step="0.01" min="0" value={this.state.speedFactor}/>
            </div>
            <div className='form-group'>
                <label>Utilization Threshold:</label>
                <input onChange={e => {
                    this.setState({utilizationThreshold: e.target.value})
                }} type="number" step="0.01" min="0" value={this.state.utilizationThreshold}/>
            </div>
            <div className='form-group'>
                <label>DHT Modulo:</label>
                <input onChange={e => {
                    this.setState({dhtModulo: e.target.value})
                }} type="number" step="1" min="0" value={this.state.dhtModulo}/>
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

    renderBalanceAlgorithmItems() {
        return this.props.store.balanceAlgorithms.map((e, i) => {
            return <MenuItem onClick={ menuItem => this.setState({balanceAlgorithm: e})} eventKey={e} key={i}
                             active={e == this.state.balanceAlgorithm}>{e}</MenuItem>
        })
    }
}
export default OptionModal;