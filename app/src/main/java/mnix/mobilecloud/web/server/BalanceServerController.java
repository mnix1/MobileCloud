

package mnix.mobilecloud.web.server;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import mnix.mobilecloud.algorithm.balance.BalanceAlgorithm;
import mnix.mobilecloud.option.Option;

public class BalanceServerController {
    private final WebServerServer webServerServer;

    public BalanceServerController(WebServerServer webServerServer) {
        this.webServerServer = webServerServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/balance")) {
            return null;
        }
        if (uri.startsWith("/balance/start")) {
            int steps = BalanceAlgorithm.findBalancePolicy(Option.getInstance().getBalanceAlgorithm()).start(webServerServer.getContext());
            return Response.newFixedLengthResponse(steps + "");
        }
        return null;
    }
}