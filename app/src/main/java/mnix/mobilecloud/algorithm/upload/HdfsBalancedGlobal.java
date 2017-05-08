package mnix.mobilecloud.algorithm.upload;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;

public class HdfsBalancedGlobal extends UploadPolicy {
    private static SecureRandom random = new SecureRandom();

    @Override
    public MachineServer getMachine(SegmentServer segmentServer) {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        MachineServer a = machineServers.get(random.nextInt(machineServers.size()));
        MachineServer b = machineServers.get(random.nextInt(machineServers.size()));
        return chooseMachine(a, b);
    }

    @Override
    public List<MachineServer> getReplicaMachines(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        int replicaSize = getMaxReplicaSize(possibleMachines.size());
        List<MachineServer> result = new ArrayList<>(replicaSize);
        for (int i = 0; i < replicaSize; i++) {
            MachineServer a = possibleMachines.get(random.nextInt(possibleMachines.size()));
            MachineServer b = possibleMachines.get(random.nextInt(possibleMachines.size()));
            MachineServer machine = chooseMachine(a, b);
            result.add(machine);
            possibleMachines.remove(machine);
        }
        return result;
    }

    @Override
    public MachineServer getReplicaMachine(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        MachineServer a = possibleMachines.get(random.nextInt(possibleMachines.size()));
        MachineServer b = possibleMachines.get(random.nextInt(possibleMachines.size()));
        return chooseMachine(a, b);
    }

    protected MachineServer chooseMachine(MachineServer a, MachineServer b) {
        int balancedPreference = (int) (100 * Option.getInstance().getBalancedPreference());
        int ret = compareMachine(a, b);
        if (ret == 0) {
            return a;
        } else if (ret < 0) {
            return (random.nextInt(100) < balancedPreference) ? a : b;
        } else {
            return (random.nextInt(100) < balancedPreference) ? b : a;
        }
    }

    protected int compareMachine(final MachineServer a,
                                 final MachineServer b) {
        double aUsedSpace = SegmentServerRepository.getUsedSpace(a.getIdentifier());
        double bUsedSpace = SegmentServerRepository.getUsedSpace(b.getIdentifier());

        double aUsedSpacePercent = aUsedSpace * 100d / (aUsedSpace + a.getFreeSpace());
        double bUsedSpacePercent = bUsedSpace * 100d / (bUsedSpace + b.getFreeSpace());
        if (a.equals(b) || Math.abs(aUsedSpacePercent - bUsedSpacePercent) < 5) {
            return 0;
        }
        return aUsedSpacePercent < bUsedSpacePercent ? -1 : 1;
    }
}
