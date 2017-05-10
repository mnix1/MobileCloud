

package mnix.mobilecloud.web.server;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import mnix.mobilecloud.algorithm.balance.BalanceAlgorithm;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.web.socket.Action;

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
            int totalSteps = steps;
            while (steps > 0) {
                steps = BalanceAlgorithm.findBalancePolicy(Option.getInstance().getBalanceAlgorithm()).start(webServerServer.getContext());
                totalSteps += steps;
                webServerServer.sendWebSocketMessage(Action.BALANCE_SEGMENTS_MOVED, "steps: " + steps);
            }
            return Response.newFixedLengthResponse(totalSteps + "");
        }
        return null;
    }
}