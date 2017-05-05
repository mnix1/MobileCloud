import {observable, computed, action} from "mobx";
const fetchMachines = function (store) {
    $.ajax({
        url: '/machine/list',
        success: data => {
            store.data = JSON.parse(data);
        }
    })
};
class MachineTableStore {
    constructor() {
        this.update = this.update.bind(this);
        this.update();
    }

    update() {
        fetchMachines(this);
    }

    @observable data = [];
}

export default MachineTableStore;