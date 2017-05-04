package mnix.mobilecloud.algorithm.upload;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;

public class Hadaps extends UploadPolicy {
    @Override
    public MachineServer getMachine(SegmentServer segmentServer) {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        final Map<String, Double> machineSpeedMap = new HashMap<>();
        double speedFactor = Option.getInstance().getSpeedFactor();
        double avgSpeed = 0;
        for (MachineServer machineServer : machineServers) {
            avgSpeed += machineServer.getSpeed();
        }
        avgSpeed /= machineServers.size();
        for (MachineServer machineServer : machineServers) {
            int segments = SegmentServerRepository.findByMachineIdentifierAndFileIdentifier(machineServer.getIdentifier(), segmentServer.getFileIdentifier()).size();
            double speed = machineServer.getSpeed() > avgSpeed ? machineServer.getSpeed() - (machineServer.getSpeed() - avgSpeed) / speedFactor
                    : machineServer.getSpeed() + (avgSpeed - machineServer.getSpeed()) / speedFactor;
            speed *= (1 + segments);
            machineSpeedMap.put(machineServer.getIdentifier(), speed);
        }
        Collections.sort(machineServers, new Comparator<MachineServer>() {
            @Override
            public int compare(MachineServer o1, MachineServer o2) {
                return machineSpeedMap.get(o1.getIdentifier()).compareTo(machineSpeedMap.get(o2.getIdentifier()));
            }
        });
        return machineServers.get(0);
    }

}
