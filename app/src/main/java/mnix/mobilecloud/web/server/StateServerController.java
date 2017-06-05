

package mnix.mobilecloud.web.server;

import com.google.gson.Gson;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.dto.StateDTO;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.FileServerRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;

import static mnix.mobilecloud.web.WebServer.getSuccessResponse;

public class StateServerController {
    private final WebServerServer webServerServer;

    public StateServerController(WebServerServer webServerServer) {
        this.webServerServer = webServerServer;
    }

    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.contains("/state")) {
            return null;
        }
        if (uri.startsWith("/state/clear")) {
            FileServerRepository.clear();
            SegmentServerRepository.clear();
        }
        if (uri.startsWith("/state/save")) {
            StateDTO state = new StateDTO();
            state.setFile(FileServerRepository.list());
            state.setMachine(MachineServerRepository.list());
            state.setSegment(SegmentServerRepository.list());
            return Response.newFixedLengthResponse(new Gson().toJson(state));
        }
        if (uri.startsWith("/state/restore")) {
            try {
                session.parseBody(new HashMap<String, String>());
            } catch (IOException | NanoHTTPD.ResponseException e) {
                e.printStackTrace();
            }
            String stateString = session.getParms().get("state");
            StateDTO state = new Gson().fromJson(stateString, StateDTO.class);
            handleRestore(state);
            return getSuccessResponse();
        }
        return null;
    }

    void handleRestore(StateDTO state) {
        FileServerRepository.clear();
        List<FileServer> file = state.getFile();
        for (int i = 0; i < file.size(); i++) {
            file.get(i).save();
        }
        MachineServerRepository.clear();
        List<MachineServer> machine = state.getMachine();
        for (int i = 0; i < machine.size(); i++) {
            machine.get(i).save();
        }
        SegmentServerRepository.clear();
        List<SegmentServer> segment = state.getSegment();
        for (int i = 0; i < segment.size(); i++) {
            segment.get(i).save();
        }
    }
}