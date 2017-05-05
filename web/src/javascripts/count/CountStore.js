import {observable, computed, action} from "mobx";
const fetchCount = function (store) {
    store.response = '';
    const start = Date.now();
    $.ajax({
        url: '/module/count?fileIdentifier=' + store.fileIdentifier + '&string=' + store.string,
        success: data => {
            store.response = data;
            store.fetchTime = Date.now() - start;
        }
    })
};
class CountStore {
    constructor() {
        this.update = this.update.bind(this);
    }

    update() {
        fetchCount(this);
    }

    @observable response = '';
    @observable fetchTime = -1;
    @observable fileIdentifier = '';
    @observable string = '';

    @computed get description() {
        return this.response != '' ? 'Founded: ' + this.response + ' results. Search took: ' + this.fetchTime + ' ms' : ''
    };
}

export default CountStore;