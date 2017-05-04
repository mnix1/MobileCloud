package mnix.mobilecloud.algorithm.upload;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.repository.server.MachineServerRepository;

public class HdfsBalancedFile extends UploadPolicy {
    private static SecureRandom random = new SecureRandom();


    @Override
    public MachineServer getMachine(SegmentServer segmentServer) {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        return machineServers.get(random.nextInt(machineServers.size()));
    }
}
