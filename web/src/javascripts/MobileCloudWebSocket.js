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
            if (msg.indexOf('FILE_UPLOAD_END')) {
                store.file.update();
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