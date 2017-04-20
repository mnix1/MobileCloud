package mnix.mobilecloud.web;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONObject;
import org.nanohttpd.fileupload.NanoFileUpload;
import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.repository.client.SegmentClientRepository;

import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class WebServer extends NanoHTTPD {
    public static final int PORT = 8080;
    private static final String MIME_CSS = "text/css";
    private static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    private static final String MIME_EOT = "application/vnd.ms-fontobject";
    private static final String MIME_HTML = "text/html";
    private static final String MIME_JS = "application/javascript";
    private static final String MIME_OTF = "application/font-sfnt";
    private static final String MIME_PLAINTEXT = "text/plain";
    private static final String MIME_PNG = "image/png";
    private static final String MIME_SVG = "image/svg+xml";
    private static final String MIME_TTF = "application/font-sfnt";
    private static final String MIME_WOFF = "application/font-woff";
    private static final String MIME_XML = "text/xml";
    private final Context context;

    NanoFileUpload uploader;

    public WebServer(Context context) {
        super(PORT);
        uploader = new NanoFileUpload(new DiskFileItemFactory());
        this.context = context;
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("MOBILE CLOUD", "initWebServer");
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        if (Method.GET.equals(method)) {
            Response response = checkClientController(session);
            if (response != null) {
                return response;
            }
            if (uri.equals("/")) {
                return checkAssets(uri + "index.html");
            } else {
                return checkAssets(uri);
            }
        }
        if (NanoFileUpload.isMultipartContent(session) && uri.equals("/upload")) {
            try {
                return serveUpload(session);
            } catch (IOException | FileUploadException e) {
                e.printStackTrace();
            }
        }
        return getForbiddenResponse("Mobile Cloud");
    }

    public Response serveUpload(IHTTPSession session) throws IOException, FileUploadException {
        Map<String, List<FileItem>> files = new HashMap<String, List<FileItem>>();
        Map<String, Object> params = new HashMap<String, Object>();
        FileItemIterator iter = uploader.getItemIterator(session);
        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            final String fileName = item.getName();
            if (fileName == null) {
                String line = new BufferedReader(new InputStreamReader(item.openStream())).readLine();
                params.put(item.getFieldName(), line);
                continue;
            }
            SegmentClientRepository.save(params, item);
            FileItem fileItem = uploader.getFileItemFactory().createItem(item.getFieldName(), item.getContentType(), item.isFormField(), fileName);
            files.put(fileItem.getFieldName(), Arrays.asList(new FileItem[]{fileItem}));
            try {
                Streams.copy(item.openStream(), fileItem.getOutputStream(), true);
            } catch (Exception e) {
            }
            fileItem.setHeaders(item.getHeaders());
        }
        return response(true);
    }

    public Response checkClientController(IHTTPSession session) {
        String uri = session.getUri();
        if (uri.equals("/fetchClientSegments")) {
            return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(SegmentClientRepository.list()));
        } else if (uri.contains("/downloadClientSegment")) {
            SegmentClient segmentClient = SegmentClientRepository.findByIdentifier(session.getParms().get("identifier"));
            if (segmentClient == null) {
                return response(false);
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(segmentClient.getData());
            Response response = new Response(Status.OK, getMimeTypeForFile(uri), inputStream, inputStream.available());
            response.addHeader("Content-disposition", "attachment; filename=" + segmentClient.getIdentifier());
            return response;
        }
        return null;
    }

    private Response response(boolean success) {
        Map<String, Boolean> response = new HashMap<String, Boolean>();
        response.put("success", success);
        return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(response));
    }

    public Response checkAssets(String uri) {
        String mime = null;
        try {
            if (uri.endsWith(".js")) {
                mime = MIME_JS;
            } else if (uri.endsWith(".css")) {
                mime = MIME_CSS;
            } else if (uri.endsWith(".html")) {
                mime = MIME_HTML;
            } else if (uri.endsWith(".eot")) {
                mime = MIME_EOT;
            } else if (uri.endsWith(".otf")) {
                mime = MIME_TTF;
            } else if (uri.endsWith(".svg")) {
                mime = MIME_SVG;
            } else if (uri.endsWith(".ttf")) {
                mime = MIME_TTF;
            } else if (uri.endsWith(".woff")) {
                mime = MIME_WOFF;
            }
            if (mime != null) {
                InputStream buffer = context.getAssets().open(uri.substring(1));
                return new Response(Status.OK, mime, buffer, (long) buffer.available());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getForbiddenResponse("Mobile Cloud");
    }


    public static Response getForbiddenResponse(String s) {
        return newFixedLengthResponse(Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: " + s);
    }
}