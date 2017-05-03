

package mnix.mobilecloud.web.client;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import java.util.List;

import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.repository.client.SegmentClientRepository;

import static mnix.mobilecloud.web.WebServer.getFailedResponse;
import static mnix.mobilecloud.web.WebServer.getSuccessResponse;

public class FileClientController {

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/file/")) {
            return null;
        }
        if (uri.startsWith("/file/delete")) {
            List<SegmentClient> segmentClients = SegmentClientRepository.findByFileIdentifier(session.getParms().get("identifier"));
            if (segmentClients == null) {
                return getFailedResponse();
            }
            for (SegmentClient segmentClient : segmentClients) {
                segmentClient.delete();
            }
            return getSuccessResponse();
        }
        return null;
    }
}