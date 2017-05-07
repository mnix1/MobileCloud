
package mnix.mobilecloud.web.server;

import com.google.gson.Gson;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;
import org.nanohttpd.protocols.http.response.StreamingResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.communication.server.SegmentServerCommunication;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;
import mnix.mobilecloud.web.socket.Action;

import static mnix.mobilecloud.network.NetworkUtil.getIpAddress;
import static mnix.mobilecloud.web.WebServer.getFailedResponse;
import static mnix.mobilecloud.web.WebServer.getSuccessResponse;
import static org.nanohttpd.protocols.http.NanoHTTPD.getMimeTypeForFile;
import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class SegmentServerController {
    private final WebServerServer webServerServer;

    public SegmentServerController(WebServerServer webServerServer) {
        this.webServerServer = webServerServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/segment/")) {
            return null;
        }
        if (uri.startsWith("/segment/update")) {
            try {
                session.parseBody(new HashMap<String, String>());
            } catch (IOException | NanoHTTPD.ResponseException e) {
                e.printStackTrace();
            }
            Map<String, String> params = session.getParms();
            return processSegmentUpdate(params);
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
                SegmentServerCommunication segmentCommunication = new SegmentServerCommunication(webServerServer.getContext());
                MachineServer machineServer = MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier());
                if (segmentCommunication.deleteSegment(segmentServer, machineServer.isMaster() ? getIpAddress() : machineServer.getIpAddress())) {
                    segmentServer.delete();
                    webServerServer.sendWebSocketMessage(Action.SEGMENT_DELETED);
                    return getSuccessResponse();
                }
            }
            return getFailedResponse();
        }
        return null;
    }

    private Response processSegmentUpdate(Map<String, String> params) {
        SegmentServer segmentServer = new SegmentServer();
        segmentServer.setIdentifier(params.get("identifier"));
        segmentServer.setFileIdentifier(params.get("fileIdentifier"));
        segmentServer.setMachineIdentifier(params.get("machineIdentifier"));
        segmentServer.setByteFrom(Long.parseLong(params.get("byteFrom")));
        segmentServer.setByteTo(Long.parseLong(params.get("byteTo")));
        segmentServer.save();
        webServerServer.sendWebSocketMessage(Action.SEGMENT_UPLOADED);
        return getSuccessResponse();
    }

    private Response processSegmentDownload(String uri, String segmentIdentifier) {
        SegmentServer segmentServer = SegmentServerRepository.findByIdentifier(segmentIdentifier);
        if (segmentServer == null) {
            return getFailedResponse();
        }
        List<SegmentServer> segmentServers = new ArrayList<>();
        segmentServers.add(segmentServer);
        SegmentServerCommunication segmentCommunication = new SegmentServerCommunication(webServerServer.getContext());
        StreamingResponse response = new StreamingResponse(Status.OK, getMimeTypeForFile(uri), segmentCommunication, segmentServer.getSize(), segmentServers);
        response.addHeader("Content-disposition", "attachment; filename=" + segmentServer.getIdentifier());
        return response;
    }
}