package mnix.mobilecloud.algorithm.upload;

import java.util.Map;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;

public abstract class UploadPolicy {
//    public abstract MachineServer getMachine(Map<String, String> params);
    public abstract MachineServer getMachine(SegmentServer segmentServer);
}
