

package mnix.mobilecloud.web.client;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.nanohttpd.fileupload.NanoFileUpload;
import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.communication.client.ClientSegmentCommunication;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.repository.client.SegmentClientRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.util.Util;

import static mnix.mobilecloud.web.WebServer.getFailedResponse;
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
        if (NanoFileUpload.isMultipartContent(session) && uri.startsWith("/segment/upload")) {
            try {
                return serveUpload(session);
            } catch (IOException | FileUploadException e) {
                e.printStackTrace();
                return getFailedResponse();
            }
        }
        if (uri.equals("/segment/list")) {
            return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(SegmentClientRepository.list()));
        }
        if (uri.startsWith("/segment/download")) {
            SegmentClient segmentClient = SegmentClientRepository.findByIdentifier(session.getParms().get("identifier"));
            if (segmentClient == null) {
                return getFailedResponse();
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(segmentClient.getData());
            Response response = new Response(Status.OK, getMimeTypeForFile(uri), inputStream, inputStream.available());
            response.addHeader("Content-disposition", "attachment; filename=" + segmentClient.getIdentifier());
            return response;
        }
        if (uri.startsWith("/segment/delete")) {
            SegmentClient segmentClient = SegmentClientRepository.findByIdentifier(session.getParms().get("identifier"));
            if (segmentClient == null) {
                return getFailedResponse();
            }
            segmentClient.delete();
            return getSuccessResponse();
        }
        if (uri.startsWith("/segment/copy")) {
            return processCopy(session);
        }
        return null;
    }

    public Response serveUpload(IHTTPSession session) throws IOException, FileUploadException {
        Util.log(this.getClass(), "serveUpload");
        Map<String, String> params = new HashMap<String, String>();
        SegmentClientRepository.save(params, clientWebServer.serverMultipart(session, params));
        return getSuccessResponse();
    }

    private Response processCopy(IHTTPSession session) {
        Map<String, String> params = session.getParms();
        String segmentIdentifier = params.get("identifier");
        String newSegmentIdentifier = params.containsKey("newIdentifier") ? params.get("newIdentifier") : segmentIdentifier;
        String destinationAddress = params.get("address");
        ClientSegmentCommunication segmentCommunication = new ClientSegmentCommunication(clientWebServer.getContext());
        return segmentCommunication.uploadSegment(SegmentClientRepository.findByIdentifier(segmentIdentifier), newSegmentIdentifier, destinationAddress) ? getSuccessResponse() : getFailedResponse();
    }
}