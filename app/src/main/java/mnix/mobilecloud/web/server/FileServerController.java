
package mnix.mobilecloud.web.server;

import com.google.gson.Gson;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.nanohttpd.fileupload.NanoFileUpload;
import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.StreamingResponse;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.communication.server.ServerSegmentCommunication;
import mnix.mobilecloud.domain.client.SegmentClient;
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

public class FileServerController {
    private final ServerWebServer serverWebServer;

    public FileServerController(ServerWebServer serverWebServer) {
        this.serverWebServer = serverWebServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/file/")) {
            return null;
        }
        if (NanoFileUpload.isMultipartContent(session) && uri.startsWith("/file/upload")) {
            try {
                serverWebServer.sendWebSocketMessage(Action.FILE_UPLOAD_START);
                serverWebServer.sendWebSocketMessage(Action.SEGMENT_UPLOAD_START);
                return serveUpload(session);
            } catch (IOException | FileUploadException e) {
                e.printStackTrace();
            }
        }
        if (uri.startsWith("/file/uploadSuccess")) {
            try {
                session.parseBody(new HashMap<String, String>());
            } catch (IOException | NanoHTTPD.ResponseException e) {
                e.printStackTrace();
            }
            return serveUploadSuccess(session.getParms());
        }
        if (uri.startsWith("/file/list")) {
            return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(FileServerRepository.list()));
        }
        if (uri.startsWith("/file/download")) {
            String fileIdentifier = session.getParms().get("identifier");
            return processFileDownload(uri, fileIdentifier);
        }
        if (uri.startsWith("/file/delete")) {
            String fileIdentifier = session.getParms().get("identifier");
            return processFileDelete(fileIdentifier);
        }
        return null;
    }

    private Response serveUpload(IHTTPSession session) throws IOException, FileUploadException {
        Map<String, String> params = new HashMap<String, String>();
        FileItemIterator iter = serverWebServer.uploader.getItemIterator(session);
        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            final String fileName = item.getName();
            if (fileName == null) {
                String line = new BufferedReader(new InputStreamReader(item.openStream())).readLine();
                params.put(item.getFieldName(), line);
                continue;
            }
            MachineServer machineServer = MachineServerRepository.findByRole(MachineRole.SLAVE).get(0);
            SegmentServer segmentServer = new SegmentServer(params);
            segmentServer.setMachineIdentifier(machineServer.getIdentifier());
            SegmentClient segmentClient = new SegmentClient(segmentServer, item);
            Boolean success = processUploadSegment(segmentClient, machineServer);
            if (!success) {
                return getFailedResponse();
            }
            segmentServer.save();
            serverWebServer.sendWebSocketMessage(Action.SEGMENT_UPLOAD_END);
            if (!params.containsKey("qqtotalparts")) {
                serveUploadSuccess(params);
            }
        }
        return getSuccessResponse();
    }

    private Boolean processUploadSegment(SegmentClient segmentClient, MachineServer machineServer) {
        ServerSegmentCommunication segmentCommunication = new ServerSegmentCommunication(serverWebServer.getContext());
        return segmentCommunication.uploadSegment(segmentClient, machineServer);
    }

    private Response serveUploadSuccess(Map<String, String> params) {
        FileServerRepository.save(params);
        serverWebServer.sendWebSocketMessage(Action.FILE_UPLOAD_END);
        return getSuccessResponse();
    }

    private Response processFileDownload(String uri, String fileIdentifier) {
        FileServer fileServer = FileServerRepository.findByIdentifier(fileIdentifier);
        if (fileServer == null) {
            return getFailedResponse();
        }
        List<SegmentServer> segmentServers = SegmentServerRepository.findByFileIdentifier(fileIdentifier);
        ServerSegmentCommunication segmentCommunication = new ServerSegmentCommunication(serverWebServer.getContext());
        StreamingResponse response = new StreamingResponse(Status.OK, getMimeTypeForFile(uri), segmentCommunication, fileServer.getSize(), segmentServers);
        response.addHeader("Content-disposition", "attachment; filename=" + fileServer.getName());
        return response;
    }

    private Response processFileDelete(String fileIdentifier) {
        FileServer fileServer = FileServerRepository.findByIdentifier(fileIdentifier);
        if (fileServer == null) {
            return getFailedResponse();
        }
        ServerSegmentCommunication segmentCommunication = new ServerSegmentCommunication(serverWebServer.getContext());
        List<SegmentServer> segmentServers = SegmentServerRepository.findByFileIdentifier(fileIdentifier);
        Boolean success = true;
        for (SegmentServer segmentServer : segmentServers) {
            Boolean localSuccess = segmentCommunication.deleteSegment(segmentServer, MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier()));
            if (!localSuccess) {
                success = false;
            } else {
                serverWebServer.sendWebSocketMessage(Action.SEGMENT_DELETED);
            }
        }
        if (success) {
            fileServer.delete();
            serverWebServer.sendWebSocketMessage(Action.FILE_DELETED);
            return getSuccessResponse();
        }
        return getFailedResponse();
    }
}