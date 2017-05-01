import {observable, computed, action} from "mobx";
import MobileCloudWebSocket from './MobileCloudWebSocket';
import FileTableStore from './file/FileTableStore';
import MachineTableStore from './machine/MachineTableStore';
import SegmentTableStore from './segment/SegmentTableStore';

class GlobalStore {
    constructor() {
        const webSocket = new MobileCloudWebSocket(this);
        this.file = new FileTableStore();
        this.machine = new MachineTableStore();
        this.segment = new SegmentTableStore();
    }
}

export default GlobalStore;