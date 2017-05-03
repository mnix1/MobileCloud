import {observable, computed, action} from "mobx";
import MobileCloudWebSocket from './MobileCloudWebSocket';
import FileTableStore from './file/FileTableStore';
import MachineTableStore from './machine/MachineTableStore';
import SegmentTableStore from './segment/SegmentTableStore';
import CountStore from './count/CountStore';

class GlobalStore {
    constructor() {
        const webSocket = new MobileCloudWebSocket(this);
        this.file = new FileTableStore();
        this.count = new CountStore();
        this.machine = new MachineTableStore();
        this.segment = new SegmentTableStore();
    }
}

export default GlobalStore;