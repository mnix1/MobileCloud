package mnix.mobilecloud.web.socket;


import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.websockets.CloseCode;
import org.nanohttpd.protocols.websockets.WebSocket;
import org.nanohttpd.protocols.websockets.WebSocketFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MobileCloudWebSocket extends WebSocket {
    public static final List<MobileCloudWebSocket> ACTIVE = new ArrayList<>();

    public MobileCloudWebSocket(IHTTPSession handshakeRequest) {
        super(handshakeRequest);
    }

    @Override
    protected void onOpen() {
        ACTIVE.add(this);
    }

    @Override
    protected void onClose(CloseCode code, String reason, boolean initiatedByRemote) {
        ACTIVE.remove(this);
    }

    @Override
    protected void onMessage(WebSocketFrame message) {
        try {
            message.setUnmasked();
            sendFrame(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPong(WebSocketFrame pong) {
    }

    @Override
    protected void onException(IOException exception) {
    }

    @Override
    protected void debugFrameReceived(WebSocketFrame frame) {
    }

    @Override
    protected void debugFrameSent(WebSocketFrame frame) {
    }
}
