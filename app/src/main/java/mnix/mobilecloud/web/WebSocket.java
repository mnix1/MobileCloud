package mnix.mobilecloud.web;

import org.nanohttpd.protocols.websockets.NanoWSD;
import org.nanohttpd.samples.websockets.DebugWebSocketServer;

import java.io.IOException;

public class WebSocket {
    public WebSocket() {
        NanoWSD ws = new DebugWebSocketServer(9090, true);
        try {
            ws.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
