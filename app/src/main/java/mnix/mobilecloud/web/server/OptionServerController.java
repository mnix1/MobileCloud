

package mnix.mobilecloud.web.server;

import com.google.gson.Gson;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.communication.server.ServerModuleCommunication;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.module.ModuleError;
import mnix.mobilecloud.module.server.ServerModuleService;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.SegmentServerRepository;

import static mnix.mobilecloud.module.ModuleUtil.getDataArg;
import static mnix.mobilecloud.web.WebServer.getFailedResponse;
import static mnix.mobilecloud.web.WebServer.getSuccessResponse;

public class OptionServerController {
    private final ServerWebServer serverWebServer;

    public OptionServerController(ServerWebServer serverWebServer) {
        this.serverWebServer = serverWebServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/option")) {
            return null;
        }
        if (uri.startsWith("/option/get")) {
            return Response.newFixedLengthResponse(new Gson().toJson(Option.getInstance()));
        }
        if (uri.startsWith("/option/set")) {
            try {
                session.parseBody(new HashMap<String, String>());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NanoHTTPD.ResponseException e) {
                e.printStackTrace();
            }
            Option.fromParams(session.getParms());
            return getSuccessResponse();
        }
        return null;
    }
}