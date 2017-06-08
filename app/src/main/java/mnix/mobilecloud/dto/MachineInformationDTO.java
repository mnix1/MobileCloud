package mnix.mobilecloud.dto;


import java.util.List;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;

public class MachineInformationDTO {
    private MachineServer machineServer;
    private List<SegmentServer> segmentServers;
    private Long usedSpace;

    public MachineServer getMachineServer() {
        return machineServer;
    }

    public void setMachineServer(MachineServer machineServer) {
        this.machineServer = machineServer;
    }

    public List<SegmentServer> getSegmentServers() {
        return segmentServers;
    }

    public void setSegmentServers(List<SegmentServer> segmentServers) {
        this.segmentServers = segmentServers;
    }

    public Long getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(Long usedSpace) {
        this.usedSpace = usedSpace;
    }

    public double getUsedSpacePercent() {
        return getUsedSpacePercent(machineServer.getCapacity(), usedSpace);
    }

    public static double getUsedSpacePercent(long capacity, long usedSpace) {
        return usedSpace * 100d /  capacity;
    }

}
