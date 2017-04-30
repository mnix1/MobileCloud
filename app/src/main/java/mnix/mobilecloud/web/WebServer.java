package mnix.mobilecloud.web;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.nanohttpd.fileupload.NanoFileUpload;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.util.Util;

import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class WebServer extends NanoHTTPD {
    protected static final String MIME_CSS = "text/css";
    protected static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    protected static final String MIME_EOT = "application/vnd.ms-fontobject";
    protected static final String MIME_HTML = "text/html";
    protected static final String MIME_JS = "application/javascript";
    protected static final String MIME_OTF = "application/font-sfnt";
    protected static final String MIME_PLAINTEXT = "text/plain";
    protected static final String MIME_PNG = "image/png";
    protected static final String MIME_SVG = "image/svg+xml";
    protected static final String MIME_TTF = "application/font-sfnt";
    protected static final String MIME_WOFF = "application/font-woff";
    protected static final String MIME_XML = "text/xml";
    protected final Context context;

    public final NanoFileUpload uploader;

    public WebServer(int port, Context context) {
        super(port);
        uploader = new NanoFileUpload(new DiskFileItemFactory());
        this.context = context;
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Util.log(this.getClass(), "initWebServer");
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
            } else if (uri.endsWith(".woff2")) {
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

    public static Response getSuccessResponse(boolean success) {
        Map<String, Boolean> response = new HashMap<String, Boolean>();
        response.put("success", success);
        return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(response));
    }

    public static Response getSuccessResponse() {
        return getSuccessResponse(true);
    }

    public static Response getFailedResponse() {
        return newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "");
    }
}