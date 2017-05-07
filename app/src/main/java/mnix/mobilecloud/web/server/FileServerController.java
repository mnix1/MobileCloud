
package mnix.mobilecloud.web.server;

import com.google.gson.Gson;

import org.apache.commons.fileupload.FileUploadException;
import org.nanohttpd.fileupload.NanoFileUpload;
import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.StreamingResponse;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.algorithm.Algorithm;
import mnix.mobilecloud.communication.server.FileServerCommunication;
import mnix.mobilecloud.communication.server.SegmentServerCommunication;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.replica.ReplicaService;
import mnix.mobilecloud.repository.client.SegmentClientRepository;
import mnix.mobilecloud.repository.server.FileServerRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;
import mnix.mobilecloud.web.socket.Action;

import static mnix.mobilecloud.web.WebServer.getFailedResponse;
import static mnix.mobilecloud.web.WebServer.getSuccessResponse;
import static org.nanohttpd.protocols.http.NanoHTTPD.getMimeTypeForFile;
import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class FileServerController {
    private final WebServerServer webServerServer;

    public FileServerController(WebServerServer webServerServer) {
        this.webServerServer = webServerServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/file/")) {
            return null;
        }
        if (NanoFileUpload.isMultipartContent(session) && uri.startsWith("/file/upload")) {
            try {
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
        byte[] data = webServerServer.serverMultipart(session, params);
        SegmentServer segmentServer = new SegmentServer(params);
        MachineServer machineServer = Algorithm.findUploadPolicy(Option.getInstance().getUploadAlgorithm()).getMachine(segmentServer);
        segmentServer.setMachineIdentifier(machineServer.getIdentifier());
        SegmentClient segmentClient = new SegmentClient(segmentServer, data);
        Boolean success = processUploadSegment(segmentClient, machineServer);
        if (!success) {
            return getFailedResponse();
        }
        segmentServer.save();
        webServerServer.sendWebSocketMessage(Action.SEGMENT_UPLOADED);
        if (!params.containsKey("qqtotalparts")) {
            serveUploadSuccess(params);
        }
        return getSuccessResponse();
    }

    private Boolean processUploadSegment(SegmentClient segmentClient, MachineServer machineServer) {
        if (machineServer.isMaster()) {
            segmentClient.save();
            return true;
        }
        SegmentServerCommunication segmentCommunication = new SegmentServerCommunication(webServerServer.getContext());
        return segmentCommunication.uploadSegment(segmentClient, machineServer.getIpAddress());
    }

    private Response serveUploadSuccess(Map<String, String> params) {
        FileServer fileServer = new FileServer(params);
        fileServer.save();
        webServerServer.sendWebSocketMessage(Action.FILE_UPLOADED);
        if (Option.getInstance().getReplicaSize() > 0) {
            ReplicaService replicaService = new ReplicaService(webServerServer.getContext());
            replicaService.processFile(fileServer);
        }
        return getSuccessResponse();
    }

    private Response processFileDownload(String uri, String fileIdentifier) {
        FileServer fileServer = FileServerRepository.findByIdentifier(fileIdentifier);
        if (fileServer == null) {
            return getFailedResponse();
        }
        List<SegmentServer> segmentServers = SegmentServerRepository.findActiveByFileIdentifierOrderById(fileIdentifier);
        SegmentServerCommunication segmentCommunication = new SegmentServerCommunication(webServerServer.getContext());
        StreamingResponse response = new StreamingResponse(Status.OK, getMimeTypeForFile(uri), segmentCommunication, fileServer.getSize(), segmentServers);
        response.addHeader("Content-disposition", "attachment; filename=" + fileServer.getName().replace(",", ""));
        return response;
    }

    private Response processFileDelete(String fileIdentifier) {
        FileServer fileServer = FileServerRepository.findByIdentifier(fileIdentifier);
        if (fileServer == null) {
            return getFailedResponse();
        }
        FileServerCommunication fileCommunication = new FileServerCommunication(webServerServer.getContext());
        List<SegmentServer> segmentServers = SegmentServerRepository.findByFileIdentifier(fileIdentifier);
        List<String> deletedFromMachine = new ArrayList<>();
        for (SegmentServer segmentServer : segmentServers) {
            if (deletedFromMachine.contains(segmentServer.getMachineIdentifier())) {
                segmentServer.delete();
            } else {
                MachineServer machineServer = MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier());
                if (machineServer.isMaster()) {
                    for (SegmentClient segmentClient : SegmentClientRepository.findByFileIdentifier(fileServer.getIdentifier())) {
                        segmentClient.delete();
                    }
                } else {
                    fileCommunication.deleteFileSegments(fileServer, machineServer.getIpAddress());
                }
                segmentServer.delete();
                deletedFromMachine.add(segmentServer.getMachineIdentifier());
            }
        }
        fileServer.delete();
        webServerServer.sendWebSocketMessage(Action.SEGMENT_DELETED);
        webServerServer.sendWebSocketMessage(Action.FILE_DELETED);
        return getSuccessResponse();
    }
}