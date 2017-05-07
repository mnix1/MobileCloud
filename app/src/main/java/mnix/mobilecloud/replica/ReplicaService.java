package mnix.mobilecloud.replica;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.algorithm.upload.UploadAlgorithm;
import mnix.mobilecloud.communication.server.SegmentServerCommunication;
import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.network.NetworkUtil;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;
import mnix.mobilecloud.web.client.SegmentClientController;

import static mnix.mobilecloud.algorithm.upload.UploadPolicy.getMaxReplicaSize;

public class ReplicaService {
    private final Context context;

    public ReplicaService(Context context) {
        this.context = context;
    }

    //first all replicas then next segment
//    public void processFile(FileServer fileServer) {
//        List<SegmentServer> segmentServers = SegmentServerRepository.findByFileIdentifier(fileServer.getIdentifier());
//        SegmentServerCommunication segmentCommunication = new SegmentServerCommunication(context);
//        for (SegmentServer segmentServer : segmentServers) {
//            MachineServer sourceMachine = MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier());
//            List<MachineServer> possibleMachines = MachineServerRepository.findByActiveAndNotIdentifier(true, sourceMachine.getIdentifier());
//            List<MachineServer> destinationMachines = Algorithm.findUploadPolicy(Option.getInstance().getUploadAlgorithm()).getReplicaMachines(segmentServer, possibleMachines);
//            for (MachineServer destinationMachine : destinationMachines) {
//                if (sourceMachine.isMaster()) {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("identifier", segmentServer.getIdentifier());
//                    params.put("address", destinationMachine.getIpAddress());
//                    SegmentClientController.processSend(params, context);
//                } else {
//                    segmentCommunication.sendSegment(segmentServer, sourceMachine.getIpAddress(),
//                            destinationMachine.isMaster() ? NetworkUtil.getIpAddress() : destinationMachine.getIpAddress());
//                }
//            }
//        }
//    }

    public void processFile(FileServer fileServer) {
        List<SegmentServer> segmentServers = SegmentServerRepository.findByFileIdentifier(fileServer.getIdentifier());
        SegmentServerCommunication segmentCommunication = new SegmentServerCommunication(context);
        Map<String, List<String>> segmentMachineExcludedMap = new HashMap<>();
        int replicaSize = getMaxReplicaSize(MachineServerRepository.findByActive(true).size() - 1);
        for (int i = 0; i < replicaSize; i++) {
            for (SegmentServer segmentServer : segmentServers) {
                MachineServer sourceMachine = MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier());
                List<String> excluded = new ArrayList<>();
                excluded.add(sourceMachine.getIdentifier());
                if (segmentMachineExcludedMap.containsKey(segmentServer.getIdentifier())) {
                    excluded = segmentMachineExcludedMap.get(segmentServer.getIdentifier());
                } else {
                    segmentMachineExcludedMap.put(segmentServer.getIdentifier(), excluded);
                }
                List<MachineServer> possibleMachines = MachineServerRepository.findByActiveAndNotIdentifiers(true, excluded);
                MachineServer destinationMachine = UploadAlgorithm.findUploadPolicy(Option.getInstance().getUploadAlgorithm()).getReplicaMachine(segmentServer, possibleMachines);
                excluded.add(destinationMachine.getIdentifier());
                if (sourceMachine.isMaster()) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("identifier", segmentServer.getIdentifier());
                    params.put("address", destinationMachine.getIpAddress());
                    SegmentClientController.processSend(params, context);
                } else {
                    segmentCommunication.sendSegment(segmentServer, sourceMachine.isMaster() ? NetworkUtil.getIpAddress() : sourceMachine.getIpAddress(),
                            destinationMachine.isMaster() ? NetworkUtil.getIpAddress() : destinationMachine.getIpAddress());
                }
            }
        }
    }
}
