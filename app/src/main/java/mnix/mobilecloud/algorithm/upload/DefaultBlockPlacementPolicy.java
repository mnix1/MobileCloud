package mnix.mobilecloud.algorithm.upload;

import java.security.SecureRandom;
import java.util.List;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.repository.server.MachineServerRepository;

public class DefaultBlockPlacementPolicy extends UploadPolicy {
    private static SecureRandom random = new SecureRandom();

    @Override
    public MachineServer getMachine() {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        return machineServers.get(random.nextInt(machineServers.size()));
    }
}
