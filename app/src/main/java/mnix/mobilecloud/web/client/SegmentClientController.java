

package mnix.mobilecloud.web.client;

import android.util.Log;

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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.repository.client.SegmentClientRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.util.Util;

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
            }
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

    public Response serveUpload(IHTTPSession session) throws IOException, FileUploadException {
        Util.log(this.getClass(), "serveUpload");
        Map<String, String> params = new HashMap<String, String>();
        FileItemIterator iter = clientWebServer.uploader.getItemIterator(session);
        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            final String fileName = item.getName();
            if (fileName == null) {
                String line = new BufferedReader(new InputStreamReader(item.openStream())).readLine();
                params.put(item.getFieldName(), line);
                continue;
            }
            SegmentClientRepository.save(params, item);
        }
        return getSuccessResponse(true);
    }

}