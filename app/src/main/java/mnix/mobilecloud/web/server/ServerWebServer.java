package mnix.mobilecloud.web.server;

import android.content.Context;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import mnix.mobilecloud.web.WebServer;

public class ServerWebServer extends WebServer {
    public static final int PORT = 8080;

    public ServerWebServer(Context context) {
        super(PORT, context);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response response = null;
        response = new FileServerController(this).serve(session);
        if (response != null) {
            return response;
        }
        response = new MachineServerController(this).serve(session);
        if (response != null) {
            return response;
        }
        String uri = session.getUri();
        if (uri.equals("/")) {
            return checkAssets(uri + "index.html");
        } else {
            return checkAssets(uri);
        }
    }
}