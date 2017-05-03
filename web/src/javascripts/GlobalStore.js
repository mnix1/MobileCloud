import {observable, computed, action} from "mobx";
import MobileCloudWebSocket from './MobileCloudWebSocket';
import FileTableStore from './file/FileTableStore';
import MachineTableStore from './machine/MachineTableStore';
import SegmentTableStore from './segment/SegmentTableStore';
import CountStore from './count/CountStore';
import OptionStore from './option/OptionStore';

class GlobalStore {
    constructor() {
        const webSocket = new MobileCloudWebSocket(this);
        this.count = new CountStore();
        this.file = new FileTableStore();
        this.machine = new MachineTableStore();
        this.segment = new SegmentTableStore();
        this.option = new OptionStore();
    }
}

export default GlobalStore;