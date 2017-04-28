
package mnix.mobilecloud.web.server;

import com.google.gson.Gson;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.util.Date;
import java.util.Map;

import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.repository.server.FileServerRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;

import static mnix.mobilecloud.web.WebServer.getSuccessResponse;
import static org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse;

public class MachineServerController {
    private final ServerWebServer serverWebServer;

    public MachineServerController(ServerWebServer serverWebServer) {
        this.serverWebServer = serverWebServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/machine/")) {
            return null;
        }
        if (uri.startsWith("/machine/update")) {
            MachineServer machineServer = getMachineServer(session);
            MachineServerRepository.update(machineServer);
            return getSuccessResponse(true);
        }
        if (uri.startsWith("/machine/list")) {
            return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(MachineServerRepository.list()));
        }
        return null;
    }

    private MachineServer getMachineServer(IHTTPSession session) {
        Map<String, String> params = session.getParms();
        MachineServer machineServer = new MachineServer();
        machineServer.setIdentifier(params.get("identifier"));
        machineServer.setRole(MachineRole.valueOf(params.get("role")));
        machineServer.setIpAddress(session.getRemoteIpAddress());
        machineServer.setLastContact(new Date());
        return machineServer;
    }
}