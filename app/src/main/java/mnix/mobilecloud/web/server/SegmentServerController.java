
package mnix.mobilecloud.web.server;

import com.google.gson.Gson;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;
import org.nanohttpd.protocols.http.response.StreamingResponse;

import java.util.ArrayList;
import java.util.List;

import mnix.mobilecloud.communication.server.ServerSegmentCommunication;
import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.repository.server.FileServerRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;
import mnix.mobilecloud.web.socket.Action;

import static mnix.mobilecloud.web.WebServer.getFailedResponse;
import static mnix.mobilecloud.web.WebServer.getSuccessResponse;
import static org.nanohttpd.protocols.http.NanoHTTPD.getMimeTypeForFile;
import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class SegmentServerController {
    private final ServerWebServer serverWebServer;

    public SegmentServerController(ServerWebServer serverWebServer) {
        this.serverWebServer = serverWebServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/segment/")) {
            return null;
        }
        if (uri.startsWith("/segment/list")) {
            return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(SegmentServerRepository.list()));
        }
        if (uri.startsWith("/segment/download")) {
            String segmentIdentifier = session.getParms().get("identifier");
            return processSegmentDownload(uri, segmentIdentifier);
        }
        if (uri.startsWith("/segment/delete")) {
            String segmentIdentifier = session.getParms().get("identifier");
            SegmentServer segmentServer = SegmentServerRepository.findByIdentifier(segmentIdentifier);
            if (segmentServer != null) {
                segmentServer.delete();
                serverWebServer.sendWebSocketMessage(Action.SEGMENT_DELETED);
                return getSuccessResponse();
            }
            return getFailedResponse();
        }
        return null;
    }

    private Response processSegmentDownload(String uri, String segmentIdentifier) {
        SegmentServer segmentServer = SegmentServerRepository.findByIdentifier(segmentIdentifier);
        if (segmentServer == null) {
            return getFailedResponse();
        }
        List<SegmentServer> segmentServers = new ArrayList<>();
        segmentServers.add(segmentServer);
        ServerSegmentCommunication segmentCommunication = new ServerSegmentCommunication(serverWebServer.getContext());
        StreamingResponse response = new StreamingResponse(Status.OK, getMimeTypeForFile(uri), segmentCommunication, segmentServer.getSize(), segmentServers);
        response.addHeader("Content-disposition", "attachment; filename=" + segmentServer.getIdentifier());
        return response;
    }
}