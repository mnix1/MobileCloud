package mnix.mobilecloud.algorithm.upload;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;

public class HdfsBalancedFile extends UploadPolicy {
    private static SecureRandom random = new SecureRandom();

    @Override
    public MachineServer getMachine(SegmentServer segmentServer) {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        MachineServer a = machineServers.get(random.nextInt(machineServers.size()));
        MachineServer b = machineServers.get(random.nextInt(machineServers.size()));
        return chooseDataNode(a, b, segmentServer.getFileIdentifier());
    }

    protected MachineServer chooseDataNode(MachineServer a, MachineServer b, String fileIdentifier) {
        int balancedPreference = (int) (100 * Option.getInstance().getBalancedPreference());
        int ret = compareDataNode(a, b, fileIdentifier);
        if (ret == 0) {
            return a;
        } else if (ret < 0) {
            return (random.nextInt(100) < balancedPreference) ? a : b;
        } else {
            return (random.nextInt(100) < balancedPreference) ? b : a;
        }
    }

    protected int compareDataNode(final MachineServer a,
                                  final MachineServer b, String fileIdentifier) {
        double aUsedSpace = SegmentServerRepository.getUsedSpace(a.getIdentifier(), fileIdentifier);
        double bUsedSpace = SegmentServerRepository.getUsedSpace(b.getIdentifier(), fileIdentifier);


        double aUsedSpacePercent = aUsedSpace * 100d / (aUsedSpace + a.getSpace());
        double bUsedSpacePercent = bUsedSpace * 100d / (bUsedSpace + b.getSpace());
        if (a.equals(b)
                || Math.abs(aUsedSpacePercent - bUsedSpacePercent) < 5) {
            return 0;
        }
        return aUsedSpacePercent < bUsedSpacePercent ? -1 : 1;
    }

    @Override
    public List<MachineServer> getReplicaMachines(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        return null;
    }
}
