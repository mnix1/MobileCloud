

package mnix.mobilecloud.web.server;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.communication.server.ModuleServerCommunication;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.module.ModuleError;
import mnix.mobilecloud.module.server.ModuleServerService;
import mnix.mobilecloud.repository.server.SegmentServerRepository;

import static mnix.mobilecloud.module.ModuleUtil.getDataArg;
import static mnix.mobilecloud.web.WebServer.getFailedResponse;

public class ModuleServerController {
    private final WebServerServer webServerServer;

    public ModuleServerController(WebServerServer webServerServer) {
        this.webServerServer = webServerServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/module")) {
            return null;
        }
        if (uri.startsWith("/module/count")) {
            Map<String, String> params = session.getParms();
            return processCount(getSegments(session), params);
        }
        return null;
    }

    public Response processCount(List<SegmentServer> segmentIdentifiers, Map<String, String> params) {
        byte[] countData = getDataArg(params);
        ModuleServerCommunication moduleCommunication = new ModuleServerCommunication(webServerServer.getContext());
        try {
            return Response.newFixedLengthResponse(ModuleServerService.count(segmentIdentifiers, countData, moduleCommunication) + "");
        } catch (ModuleError moduleError) {
            return getFailedResponse();
        }
    }

    private List<SegmentServer> getSegments(IHTTPSession session) {
        Map<String, String> params = session.getParms();
        List<SegmentServer> segmentServers = new ArrayList<>();
        if (params.containsKey("segmentIdentifier")) {
            List<String> segmentIdentifiers = session.getParameters().get("segmentIdentifier");
            segmentServers = SegmentServerRepository.findByIdentifiers(segmentIdentifiers);
        } else if (params.containsKey("fileIdentifier")) {
            String fileIdentifier = session.getParms().get("fileIdentifier");
            segmentServers = SegmentServerRepository.findActiveByFileIdentifierOrderById(fileIdentifier);
        }
        return segmentServers;
    }
}