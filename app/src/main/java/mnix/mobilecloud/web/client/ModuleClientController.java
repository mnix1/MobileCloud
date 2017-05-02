

package mnix.mobilecloud.web.client;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import mnix.mobilecloud.module.client.ClientModuleService;
import mnix.mobilecloud.repository.client.SegmentClientRepository;

import static mnix.mobilecloud.module.ModuleUtil.getDataArg;
import static mnix.mobilecloud.web.WebServer.getFailedResponse;

public class ModuleClientController {
    private final ClientWebServer clientWebServer;

    public ModuleClientController(ClientWebServer clientWebServer) {
        this.clientWebServer = clientWebServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/module")) {
            return null;
        }
        if (uri.startsWith("/module/count")) {
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
        return Response.newFixedLengthResponse(ClientModuleService.count(segmentIdentifiers, countData) + "");
    }

    public Response processIndex(Map<String, String> params) {
        byte[] countData = getDataArg(params);
        return Response.newFixedLengthResponse(ClientModuleService.index(params.get("identifier"), countData) + "");
    }


}