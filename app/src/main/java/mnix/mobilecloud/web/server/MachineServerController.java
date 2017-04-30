
package mnix.mobilecloud.web.server;

import com.google.gson.Gson;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.repository.server.FileServerRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.web.socket.Action;

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
            try {
                session.parseBody(new HashMap<String, String>());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NanoHTTPD.ResponseException e) {
                e.printStackTrace();
            }
            MachineServer machineServer = getMachineServer(session);
            MachineServerRepository.update(machineServer);
            serverWebServer.sendWebSocketMessage(Action.MACHINE_UPDATE, null);
            return getSuccessResponse();
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