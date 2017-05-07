package mnix.mobilecloud.web.client;

import android.content.Context;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.WebServer;
import mnix.mobilecloud.web.socket.WebSocketServer;

public class WebServerClient extends WebServer {
    public static final int PORT = 8090;

    public WebServerClient(Context context) {
        super(PORT, context);
    }

    public WebServerClient(Context context, WebSocketServer webSocketServer) {
        super(PORT, context, webSocketServer);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Util.log(this.getClass(), "serve", "uri: " + uri);
        Response response = null;
        response = new FileClientController().serve(session);
        if (response != null) {
            return response;
        }
        response = new MachineClientController().serve(session);
        if (response != null) {
            return response;
        }
        response = new SegmentClientController(this).serve(session);
        if (response != null) {
            return response;
        }
        response = new ModuleClientController(this).serve(session);
        if (response != null) {
            return response;
        }
        if (uri.equals("/")) {
            return checkAssets(uri + "index.html");
        } else {
            return checkAssets(uri);
        }
    }
}