package mnix.mobilecloud.web.server;

import android.content.Context;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.WebServer;
import mnix.mobilecloud.web.socket.WebSocketServer;

public class WebServerServer extends WebServer {
    public static final int PORT = 8080;

    public WebServerServer(Context context, WebSocketServer webSocketServer) {
        super(PORT, context, webSocketServer);
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
        response = new SegmentServerController(this).serve(session);
        if (response != null) {
            return response;
        }
        response = new ModuleServerController(this).serve(session);
        if (response != null) {
            return response;
        }
        response = new OptionServerController(this).serve(session);
        if (response != null) {
            return response;
        }
        if (uri.equals("/") || uri.equals("/machine") || uri.equals("/segment")) {
            return checkAssets("/index.html");
        } else {
            return checkAssets(uri);
        }
    }
}