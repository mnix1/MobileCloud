package mnix.mobilecloud.web.server;

import android.content.Context;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.WebServer;
import mnix.mobilecloud.web.socket.Action;
import mnix.mobilecloud.web.socket.ServerWebSocket;

public class ServerWebServer extends WebServer {
    public static final int PORT = 8080;

    private final ServerWebSocket serverWebSocket;
    private final Context context;

    public ServerWebServer(Context context, ServerWebSocket serverWebSocket) {
        super(PORT, context);
        this.context = context;
        this.serverWebSocket = serverWebSocket;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Util.log(this.getClass(), "serve", "uri: " + uri);
        Response response = null;
        response = new FileServerController(this).serve(session);
        if (response != null) {
            return response;
        }
        response = new MachineServerController(this).serve(session);
        if (response != null) {
            return response;
        }
        if (uri.equals("/")) {
            return checkAssets(uri + "index.html");
        } else {
            return checkAssets(uri);
        }
    }

    public void sendWebSocketMessage(Action action, String payload) {
        serverWebSocket.send(action, payload);
    }

    public void sendWebSocketMessage(Action action) {
        sendWebSocketMessage(action, null);
    }

    public Context getContext() {
        return context;
    }
}