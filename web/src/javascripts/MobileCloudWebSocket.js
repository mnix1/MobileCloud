function findUri() {
    return 'ws://' + location.hostname + ':9080/';
}
class MobileCloudWebSocket {
    constructor(store) {
        const uri = findUri();
        this.webSocket = new WebSocket(uri);
        this.webSocket.onopen = evt => {
            console.log('onopen', evt);
            this.pingInterval = setInterval(() => {
                this.send('PING')
            }, 1000)
        };
        this.webSocket.onclose = evt => {
            console.log('onclose', evt);
            clearInterval(this.pingInterval);
        };
        this.webSocket.onmessage = evt => {
            const msg = evt.data;
            if (msg.indexOf('PING') != -1) {
                return;
            }
            console.log('onmessage', evt);
            if (msg.indexOf('FILE_UPLOAD_END') != -1 || msg.indexOf('FILE_DELETED') != -1) {
                return store.file.update();
            }
            if (msg.indexOf('MACHINE_NEW') != -1 || msg.indexOf('MACHINE_UPDATED') != -1 || msg.indexOf('MACHINE_DELETED') != -1
                || msg.indexOf('MACHINE_CONNECTED') != -1 || msg.indexOf('MACHINE_DISCONNECTED') != -1) {
                return store.machine.update();
            }
            if (msg.indexOf('SEGMENT_UPLOAD_END') != -1 || msg.indexOf('SEGMENT_DELETED') != -1) {
                return store.segment.update();
            }
        };
        this.webSocket.onerror = function (evt) {
            console.log('onerror', evt);
        };
    }

    send(message) {
        this.webSocket.send(message);
    }

    getWebSocket() {
        return this.webSocket;
    };
}
export default MobileCloudWebSocket;