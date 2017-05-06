package mnix.mobilecloud.algorithm.upload;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;

public class HadapsRandom extends UploadPolicy {
    private static SecureRandom random = new SecureRandom();

    @Override
    public MachineServer getMachine(SegmentServer segmentServer) {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        final Map<String, Double> machineSpeedMap = new HashMap<>();
        final Map<Double, MachineServer> machineLimitMap = new HashMap<>();
        double speedFactor = Option.getInstance().getSpeedFactor();
        double factor = 0;
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
            factor += 1d / speed;
            machineSpeedMap.put(machineServer.getIdentifier(), speed);
        }
        factor = 1 / factor;
        double total = 0;
        for (MachineServer machineServer : machineServers) {
            double percent = factor * 100 / machineSpeedMap.get(machineServer.getIdentifier());
            total += percent;
            machineLimitMap.put(percent, machineServer);
        }
        int rand = random.nextInt((int) Math.floor(total));
        double prev = 0;
        for (Double d : machineLimitMap.keySet()) {
            if (rand <= prev + d) {
                return machineLimitMap.get(d);
            }
            prev = d;
        }
        return machineServers.get(0);
    }

    @Override
    public List<MachineServer> getReplicaMachines(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        return null;
    }
}
