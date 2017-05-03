
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
import mnix.mobilecloud.communication.server.ServerMachineCommunication;
import mnix.mobilecloud.domain.client.MachineClient;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.repository.client.MachineClientRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.web.socket.Action;

import static mnix.mobilecloud.web.WebServer.getFailedResponse;
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
            serverWebServer.sendWebSocketMessage(Action.MACHINE_UPDATED, null);
            return getSuccessResponse();
        }
        if (uri.startsWith("/machine/list")) {
            return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(MachineServerRepository.list()));
        }
        if (uri.startsWith("/machine/delete")) {
            String machineIdentifier = session.getParms().get("identifier");
            MachineServer machineServer = MachineServerRepository.findByIdentifier(machineIdentifier);
            if (machineServer != null) {
                machineServer.delete();
                serverWebServer.sendWebSocketMessage(Action.MACHINE_DELETED);
                return getSuccessResponse();
            }
            return getFailedResponse();
        }
        if (uri.startsWith("/machine/connect")) {
            return setActive(session, true);
        }
        if (uri.startsWith("/machine/disconnect")) {
            return setActive(session, false);
        }
        if (uri.startsWith("/machine/refresh")) {
            return processRefresh(session);
        }
        return null;
    }

    private Response setActive(IHTTPSession session, boolean active) {
        String machineIdentifier = session.getParms().get("identifier");
        MachineServer machineServer = MachineServerRepository.findByIdentifier(machineIdentifier);
        if (machineServer != null) {
            machineServer.setActive(active);
            machineServer.save();
            if (active) {
                serverWebServer.sendWebSocketMessage(Action.MACHINE_CONNECTED);
            } else {
                serverWebServer.sendWebSocketMessage(Action.MACHINE_DISCONNECTED);
            }
            return getSuccessResponse();
        }
        return getFailedResponse();
    }

    private Response processRefresh(IHTTPSession session) {
        String machineIdentifier = session.getParms().get("identifier");
        MachineServer machineServer = MachineServerRepository.findByIdentifier(machineIdentifier);
        if (machineServer != null) {
            MachineClient machineClient;
            if (machineServer.isMaster()) {
                MachineClientRepository.update();
                machineClient = MachineClientRepository.get();
            } else {
                ServerMachineCommunication machineCommunication = new ServerMachineCommunication(serverWebServer.getContext());
                machineClient = machineCommunication.getMachine(machineServer);
                machineServer.setLastContact(new Date());
            }
            machineServer.setSpeed(machineClient.getSpeed());
            machineServer.setSpace(machineClient.getSpace());
            machineServer.save();
            serverWebServer.sendWebSocketMessage(Action.MACHINE_UPDATED);
            return getSuccessResponse();
        }
        return getFailedResponse();
    }

    private MachineServer getMachineServer(IHTTPSession session) {
        Map<String, String> params = session.getParms();
        MachineServer machineServer = new MachineServer();
        machineServer.setIdentifier(params.get("identifier"));
        machineServer.setRole(MachineRole.valueOf(params.get("role")));
        machineServer.setIpAddress(session.getRemoteIpAddress());
        machineServer.setLastContact(new Date());
        machineServer.setName(params.get("name"));
        machineServer.setDevice(params.get("device"));
        machineServer.setSystem(params.get("system"));
        machineServer.setSpeed(Long.parseLong(params.get("speed")));
        machineServer.setSpace(Long.parseLong(params.get("space")));
        return machineServer;
    }
}