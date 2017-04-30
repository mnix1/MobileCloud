import {observable, computed, action} from "mobx";
import MobileCloudWebSocket from './MobileCloudWebSocket';
import FileTableStore from './file/FileTableStore';

class GlobalStore {
    constructor() {
        const webSocket = new MobileCloudWebSocket(this);
        this.file = new FileTableStore();
    }
}

export default GlobalStore;