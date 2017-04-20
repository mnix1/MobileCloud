import {observable, computed, action} from "mobx";
import MobileCloudWebSocket from './MobileCloudWebSocket';
class GlobalStore {
    constructor() {
        const webSocket = new MobileCloudWebSocket();
    }

    @observable current = Date.now();

    @computed get elapsedTime() {
        return (this.current - this.start) + "seconds";
    }

    @action tick() {
        this.current = Date.now()
    }
}

export default GlobalStore;