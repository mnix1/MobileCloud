package mnix.mobilecloud.web.socket;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.websockets.NanoWSD;
import org.nanohttpd.protocols.websockets.WebSocket;

import java.io.IOException;

public class WebSocketServer extends NanoWSD {
    public static final int PORT = 9080;
    private MobileCloudWebSocket webSocket;

    public WebSocketServer() {
        super(PORT);
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        webSocket = new MobileCloudWebSocket(handshake);
        return webSocket;
    }

    public void send(Action action, String payload) {
//        for (MobileCloudWebSocket mobileCloudWebSocket : MobileCloudWebSocket.ACTIVE) {
//            try {
//                if (mobileCloudWebSocket.isOpen()) {
//                    mobileCloudWebSocket.send(action.toString());
//                    if (payload != null) {
//                        mobileCloudWebSocket.send(payload);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

}
