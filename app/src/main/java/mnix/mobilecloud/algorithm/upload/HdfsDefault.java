package mnix.mobilecloud.algorithm.upload;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.repository.server.MachineServerRepository;

public class HdfsDefault extends UploadPolicy {
    private static SecureRandom random = new SecureRandom();

    @Override
    public MachineServer getMachine(Map<String, String> params) {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        return machineServers.get(random.nextInt(machineServers.size()));
    }
}
