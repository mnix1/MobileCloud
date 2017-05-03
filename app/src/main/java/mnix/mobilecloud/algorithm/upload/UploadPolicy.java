package mnix.mobilecloud.algorithm.upload;

import java.util.Map;

import mnix.mobilecloud.domain.server.MachineServer;

public abstract class UploadPolicy {
    public abstract MachineServer getMachine(Map<String, String> params);
}
