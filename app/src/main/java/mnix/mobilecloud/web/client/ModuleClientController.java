

package mnix.mobilecloud.web.client;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.module.client.ModuleClientService;

import static mnix.mobilecloud.module.ModuleUtil.getDataArg;

public class ModuleClientController {
    private final WebServerClient webServerClient;

    public ModuleClientController(WebServerClient webServerClient) {
        this.webServerClient = webServerClient;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/module")) {
            return null;
        }
        if (uri.startsWith("/module/count")) {
            try {
                session.parseBody(new HashMap<String, String>());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NanoHTTPD.ResponseException e) {
                e.printStackTrace();
            }
            List<String> segmentIdentifiers = session.getParameters().get("identifier");
            Map<String, String> params = session.getParms();
            return processCount(segmentIdentifiers, params);
        }
        if (uri.startsWith("/module/index")) {
            Map<String, String> params = session.getParms();
            return processIndex(params);
        }
        return null;
    }

    public Response processCount(List<String> segmentIdentifiers, Map<String, String> params) {
        byte[] countData = getDataArg(params);
        return Response.newFixedLengthResponse(ModuleClientService.count(segmentIdentifiers, countData) + "");
    }

    public Response processIndex(Map<String, String> params) {
        byte[] countData = getDataArg(params);
        return Response.newFixedLengthResponse(ModuleClientService.index(params.get("identifier"), countData) + "");
    }


}