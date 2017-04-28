

package mnix.mobilecloud.web.client;

import com.google.gson.Gson;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.ByteArrayInputStream;

import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.repository.client.SegmentClientRepository;

import static mnix.mobilecloud.web.WebServer.getSuccessResponse;
import static org.nanohttpd.protocols.http.NanoHTTPD.getMimeTypeForFile;
import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class SegmentClientController {
    private final ClientWebServer clientWebServer;

    public SegmentClientController(ClientWebServer clientWebServer) {
        this.clientWebServer = clientWebServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/segment/")) {
            return null;
        }
        if (uri.equals("/segment/list")) {
            return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(SegmentClientRepository.list()));
        } else if (uri.startsWith("/segment/download")) {
            SegmentClient segmentClient = SegmentClientRepository.findByIdentifier(session.getParms().get("identifier"));
            if (segmentClient == null) {
                return getSuccessResponse(false);
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(segmentClient.getData());
            Response response = new Response(Status.OK, getMimeTypeForFile(uri), inputStream, inputStream.available());
            response.addHeader("Content-disposition", "attachment; filename=" + segmentClient.getIdentifier());
            return response;
        }
        return null;
    }
}