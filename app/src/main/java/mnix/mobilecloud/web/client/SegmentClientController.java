

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

import mnix.mobilecloud.communication.client.SegmentClientCommunication;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.repository.client.MachineClientRepository;
import mnix.mobilecloud.repository.client.SegmentClientRepository;
import mnix.mobilecloud.util.FileUtil;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.socket.Action;

import static mnix.mobilecloud.web.WebServer.getFailedResponse;
import static mnix.mobilecloud.web.WebServer.getSuccessResponse;
import static org.nanohttpd.protocols.http.NanoHTTPD.getMimeTypeForFile;
import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class SegmentClientController {
    private final WebServerClient webServerClient;

    public SegmentClientController(WebServerClient webServerClient) {
        this.webServerClient = webServerClient;
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
            FileUtil.delete(segmentClient);
            segmentClient.delete();
            return getSuccessResponse();
        }
        if (uri.startsWith("/segment/send")) {
            return processSend(session.getParms(), webServerClient.getContext()) ? getSuccessResponse() : getFailedResponse();
        }
        return null;
    }

    public Response serveUpload(IHTTPSession session) throws IOException, FileUploadException {
        Util.log(this.getClass(), "serveUpload");
        Map<String, String> params = new HashMap<String, String>();
        SegmentClient segmentClient = new SegmentClient(params, webServerClient.serverMultipart(session, params));
        Util.log(this.getClass(), "serveUpload", "segmentClient: " + segmentClient);
        if (session.getParms().containsKey("notifyServer")) {
            if (MachineClientRepository.isServer()) {
                SegmentServer segmentServer = new SegmentServer(segmentClient);
                segmentServer.save();
                webServerClient.sendWebSocketMessage(Action.SEGMENT_UPLOADED);
                segmentClient.save();
            } else {
                SegmentClientCommunication segmentCommunication = new SegmentClientCommunication(webServerClient.getContext());
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
        byte[] segmentClientData = segmentClient.getData();
        if (params.containsKey("newIdentifier")) {
            segmentClient.setIdentifier(params.get("newIdentifier"));
        } else {
            segmentClient.setIdentifierFromFileIdentifier(segmentClient.getFileIdentifier());
        }
        Long byteFrom = 0L;
        Long byteTo = segmentClient.getSize();
        if (params.containsKey("byteFrom")) {
            byteFrom = Long.parseLong(params.get("byteFrom"));
            segmentClient.setByteFrom(byteFrom);
        }
        if (params.containsKey("byteTo")) {
            byteTo = Long.parseLong(params.get("byteTo"));
            segmentClient.setByteTo(byteTo);
        }
        byte[] data = Arrays.copyOfRange(segmentClientData, byteFrom.intValue(), byteTo.intValue());
        segmentClient.setData(data);
        String destinationAddress = params.get("address");
        SegmentClientCommunication segmentCommunication = new SegmentClientCommunication(context);
        return segmentCommunication.uploadSegment(segmentClient, destinationAddress);
    }
}