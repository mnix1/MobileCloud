

package mnix.mobilecloud.web.client;

import android.content.Context;

import com.google.gson.Gson;

import org.apache.commons.fileupload.FileUploadException;
import org.nanohttpd.fileupload.NanoFileUpload;
import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mnix.mobilecloud.communication.client.ClientSegmentCommunication;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.repository.client.MachineClientRepository;
import mnix.mobilecloud.repository.client.SegmentClientRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.socket.Action;

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
        if (uri.startsWith("/segment/send")) {
            return processSend(session.getParms(), clientWebServer.getContext()) ? getSuccessResponse() : getFailedResponse();
        }
        return null;
    }

    public Response serveUpload(IHTTPSession session) throws IOException, FileUploadException {
        Util.log(this.getClass(), "serveUpload");
        Map<String, String> params = new HashMap<String, String>();
        SegmentClient segmentClient = new SegmentClient(params, clientWebServer.serverMultipart(session, params));
        if (session.getParms().containsKey("notifyServer")) {
            if (MachineClientRepository.isServer()) {
                SegmentServer segmentServer = new SegmentServer(segmentClient);
                segmentServer.save();
                clientWebServer.sendWebSocketMessage(Action.SEGMENT_UPLOADED);
                segmentClient.save();
            } else {
                ClientSegmentCommunication segmentCommunication = new ClientSegmentCommunication(clientWebServer.getContext());
                segmentCommunication.updateSegment(segmentClient);
            }
        } else {
            segmentClient.save();
        }
        return getSuccessResponse();
    }

    public static Boolean processSend(Map<String, String> params, Context context) {
        String segmentIdentifier = params.get("identifier");
        SegmentClient segmentClient = SegmentClientRepository.findByIdentifier(segmentIdentifier);
        if (params.containsKey("newIdentifier")) {
            segmentClient.setIdentifier("newIdentifier");
        } else {
            segmentClient.setIdentifierFromFileIdentifier(segmentClient.getFileIdentifier());
        }
        Long byteFrom = segmentClient.getByteFrom();
        Long byteTo = segmentClient.getByteTo();
        if (params.containsKey("byteFrom")) {
            byteFrom = Long.parseLong(params.get("byteFrom"));
            segmentClient.setByteFrom(byteFrom);
        }
        if (params.containsKey("byteTo")) {
            byteTo = Long.parseLong(params.get("byteTo"));
            segmentClient.setByteTo(byteTo);
        }
        byte[] data = Arrays.copyOfRange(segmentClient.getData(), byteFrom.intValue(), byteTo.intValue());
        segmentClient.setData(data);
        String destinationAddress = params.get("address");
        ClientSegmentCommunication segmentCommunication = new ClientSegmentCommunication(context);
        return segmentCommunication.uploadSegment(segmentClient, destinationAddress);

    }
}