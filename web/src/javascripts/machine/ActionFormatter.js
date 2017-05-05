import React, {Component} from "react";
import {browserHistory} from 'react-router'
class ConnectButton extends Component {
    handleClick() {
        $.ajax({
            url: "/machine/connect",
            data: {identifier: this.props.identifier},
            success: data => {
                // this.props.onComplete();
            }
        });
    }

    render() {
        return <i
            className={`fa fa-plug ${this.props.className ? this.props.className : ''}`}
            onClick={this.handleClick.bind(this)}>{this.props.children}</i>
    }
}
class DisconnectButton extends Component {
    handleClick() {
        $.ajax({
            url: "/machine/disconnect",
            data: {identifier: this.props.identifier},
            success: data => {
                // this.props.onComplete();
            }
        });
    }

    render() {
        return <i
            className={`fa fa-ban ${this.props.className ? this.props.className : ''}`}
            onClick={this.handleClick.bind(this)}>{this.props.children}</i>
    }
}
class RefreshButton extends Component {
    handleClick() {
        $.ajax({
            url: "/machine/refresh",
            data: {identifier: this.props.identifier},
            success: data => {
                // this.props.onComplete();
            }
        });
    }

    render() {
        return <i
            className={`fa fa-refresh ${this.props.className ? this.props.className : ''}`}
            onClick={this.handleClick.bind(this)}>{this.props.children}</i>
    }
}
class DeleteButton extends Component {
    handleClick() {
        $.ajax({
            url: "/machine/delete",
            data: {identifier: this.props.identifier},
            success: data => {
                // this.props.onComplete();
            }
        });
    }

    render() {
        return <i
            className={`fa fa-trash ${this.props.className ? this.props.className : ''}`}
            onClick={this.handleClick.bind(this)}>{this.props.children}</i>
    }
}
function ActionFormatter(config) {
    return (cell, row, enumObject, index) => {
        return (
            <div className="action">
                {row.active
                    ? <DisconnectButton identifier={row.identifier}>Disconnect</DisconnectButton>
                    : <ConnectButton identifier={row.identifier}>Connect</ConnectButton> }
                <br/>
                <RefreshButton identifier={row.identifier}>Refresh</RefreshButton>
                <br/>
                <DeleteButton identifier={row.identifier}>Delete</DeleteButton>
            </div>
        )
    };
}
export default ActionFormatter;