package mnix.mobilecloud.algorithm.upload;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.repository.server.MachineServerRepository;

public class HdfsDefault extends UploadPolicy {
    private static SecureRandom random = new SecureRandom();

    @Override
    public MachineServer getMachine(SegmentServer segmentServer) {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        return machineServers.get(random.nextInt(machineServers.size()));
    }

    @Override
    public List<MachineServer> getReplicaMachines(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        int replicaSize = getMaxReplicaSize(possibleMachines.size());
        List<MachineServer> result = new ArrayList<>(replicaSize);
        for (int i = 0; i < replicaSize; i++) {
            int index = random.nextInt(possibleMachines.size());
            result.add(possibleMachines.get(index));
            possibleMachines.remove(index);
        }
        return result;
    }
}
