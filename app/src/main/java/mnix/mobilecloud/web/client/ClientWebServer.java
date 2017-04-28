package mnix.mobilecloud.web.client;

import android.content.Context;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import mnix.mobilecloud.web.WebServer;

public class ClientWebServer extends WebServer {
    public static final int PORT = 8090;

    public ClientWebServer(Context context) {
        super(PORT, context);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response response = null;
        response = new SegmentClientController(this).serve(session);
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