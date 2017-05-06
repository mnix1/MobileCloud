package mnix.mobilecloud.replica;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        int replicaSize = Math.min(Option.getInstance().getReplicaSize(), machineServers.size() - 1);
        List<SegmentServer> segmentServers = SegmentServerRepository.findByFileIdentifier(fileServer.getIdentifier());
        ServerSegmentCommunication segmentCommunication = new ServerSegmentCommunication(context);
        for (SegmentServer segmentServer : segmentServers) {
            MachineServer machineServer = MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier());
            List<MachineServer> possibleMachines = MachineServerRepository.findByActiveAndNotIdentifier(true, machineServer.getIdentifier());
            for (int i = 0; i < replicaSize; i++) {
                MachineServer destinationMachine = possibleMachines.get(0);
                possibleMachines.remove(destinationMachine);
                if (machineServer.isMaster()) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("identifier", segmentServer.getIdentifier());
                    params.put("address", destinationMachine.getIpAddress());
                    SegmentClientController.processSend(params, context);
                } else {
                    segmentCommunication.sendSegment(segmentServer, machineServer.getIpAddress(),
                            destinationMachine.isMaster() ? NetworkUtil.getInet4Address().toString().replace("/", "") : destinationMachine.getIpAddress());
                }
            }
        }
    }
}
