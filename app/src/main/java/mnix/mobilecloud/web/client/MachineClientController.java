
package mnix.mobilecloud.web.client;

import com.google.gson.Gson;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import mnix.mobilecloud.repository.client.MachineClientRepository;

import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class MachineClientController {
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/machine/")) {
            return null;
        }
        if (uri.startsWith("/machine/get")) {
            return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(MachineClientRepository.get()));
        }
        return null;
    }
}