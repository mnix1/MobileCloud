package mnix.mobilecloud.replica;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.algorithm.Algorithm;
import mnix.mobilecloud.communication.server.ServerSegmentCommunication;
import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.network.NetworkUtil;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;
import mnix.mobilecloud.web.client.SegmentClientController;

public class ReplicaService {
    private final Context context;

    public ReplicaService(Context context) {
        this.context = context;
    }

    public void processFile(FileServer fileServer) {
        List<SegmentServer> segmentServers = SegmentServerRepository.findByFileIdentifier(fileServer.getIdentifier());
        ServerSegmentCommunication segmentCommunication = new ServerSegmentCommunication(context);
        for (SegmentServer segmentServer : segmentServers) {
            MachineServer sourceMachine = MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier());
            List<MachineServer> possibleMachines = MachineServerRepository.findByActiveAndNotIdentifier(true, sourceMachine.getIdentifier());
            List<MachineServer> destinationMachines = Algorithm.findUploadPolicy(Option.getInstance().getUploadAlgorithm()).getReplicaMachines(segmentServer, possibleMachines);
            for (MachineServer destinationMachine : destinationMachines) {
                possibleMachines.remove(destinationMachine);
                if (sourceMachine.isMaster()) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("identifier", segmentServer.getIdentifier());
                    params.put("address", destinationMachine.getIpAddress());
                    SegmentClientController.processSend(params, context);
                } else {
                    segmentCommunication.sendSegment(segmentServer, sourceMachine.getIpAddress(),
                            destinationMachine.isMaster() ? NetworkUtil.getInet4Address().toString().replace("/", "") : destinationMachine.getIpAddress());
                }
            }
        }
    }
}
