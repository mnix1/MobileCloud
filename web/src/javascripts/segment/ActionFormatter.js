import React, {Component} from "react";
import {browserHistory} from 'react-router'
class DownloadButton extends Component {
    handleClick() {
        location.href = `/segment/download?identifier=${this.props.identifier}`;
    }

    render() {
        return <i
            className={`fa fa-download ${this.props.className ? this.props.className : ''}`}
            onClick={this.handleClick.bind(this)}>{this.props.children}</i>
    }
}
class DeleteButton extends Component {
    handleClick() {
        $.ajax({
            url: "/segment/delete",
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
                <DownloadButton identifier={row.identifier}>Download</DownloadButton>
                <DeleteButton identifier={row.identifier}>Delete</DeleteButton>
            </div>
        )
    };
}
export default ActionFormatter;