package mnix.mobilecloud.algorithm.upload;

import java.util.List;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.option.Option;

public abstract class UploadPolicy {
    public abstract MachineServer getMachine(SegmentServer segmentServer);

    public abstract List<MachineServer> getReplicaMachines(SegmentServer segmentServer, List<MachineServer> possibleMachines);

    protected int getMaxReplicaSize(int max) {
        return Math.min(max, Option.getInstance().getReplicaSize());
    }
}
