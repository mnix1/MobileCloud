import {observable, computed, action} from "mobx";
const fetchFiles = function (store) {
    $.ajax({
        url: '/file/list',
        success: data => {
            store.data = JSON.parse(data);
        }
    })
};
class FileTableStore {
    constructor() {
        this.update = this.update.bind(this);
        this.update();
    }

    update() {
        fetchFiles(this);
    }

    @observable data = [];
}

export default FileTableStore;