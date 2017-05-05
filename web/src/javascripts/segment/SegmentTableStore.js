import {observable, computed, action} from "mobx";
import _ from "lodash";
const fetchSegments = function (store) {
    $.ajax({
        url: '/segment/list',
        success: data => {
            store.data = JSON.parse(data);
        }
    })
};
class SegmentTableStore {
    constructor() {
        this.update = this.update.bind(this);
        this.update();
    }

    update() {
        fetchSegments(this);
    }

    handleSearch(id, value) {
        this.search[id] = value;
    }

    @observable data = [];
    @observable search = {identifier: '', fileIdentifier: '', machineIdentifier: ''};

    @computed get rows() {
        let rows = this.data;
        _.forEach(this.search, (v, k) => {
            if(v!=''){
                rows = rows.filter(e => _.includes(e[k], v));
            }
        });
        return rows;
    };
}

export default SegmentTableStore;