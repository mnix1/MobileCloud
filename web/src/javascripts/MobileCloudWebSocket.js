function findUri() {
    return 'ws://' + location.hostname + ':9080/';
}
class MobileCloudWebSocket {
    constructor() {
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
            if (evt.data.indexOf('PING') != -1) {
                return;
            }
            console.log('onmessage', evt);
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