
package mnix.mobilecloud.web.server;

import com.google.gson.Gson;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.nanohttpd.fileupload.NanoFileUpload;
import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import mnix.mobilecloud.repository.client.SegmentClientRepository;
import mnix.mobilecloud.repository.server.FileServerRepository;

import static mnix.mobilecloud.web.WebServer.getSuccessResponse;
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
        } else if (uri.contains("/file/download")) {

        }
        return null;
    }

    public Response serveUpload(IHTTPSession session) throws IOException, FileUploadException {
        Map<String, String> params = new HashMap<String, String>();
//        int available = session.getInputStream().available();
//        byte[] bytes = new byte[available];
//        session.getInputStream().read(bytes);
//        String msg = new String(bytes);
        FileItemIterator iter = serverWebServer.uploader.getItemIterator(session);
        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            final String fileName = item.getName();
            if (fileName == null) {
                String line = new BufferedReader(new InputStreamReader(item.openStream())).readLine();
                params.put(item.getFieldName(), line);
                continue;
            }
            SegmentClientRepository.save(params, item);
            if (!params.containsKey("qqtotalparts")) {
                serveUploadSuccess(params);
            }
        }
        return getSuccessResponse(true);
    }

    public Response serveUploadSuccess(Map<String, String> params) {
        FileServerRepository.save(params);
        return getSuccessResponse(true);
    }
}