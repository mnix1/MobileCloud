package mnix.mobilecloud.dto;


import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;

public class SegmentMove {
    private MachineServer source;
    private MachineServer target;
    private SegmentServer segmentServer;

    public SegmentMove(MachineServer source, MachineServer target, SegmentServer segmentServer) {
        this.source = source;
        this.target = target;
        this.segmentServer = segmentServer;
    }

    public MachineServer getSource() {
        return source;
    }

    public void setSource(MachineServer source) {
        this.source = source;
    }

    public MachineServer getTarget() {
        return target;
    }

    public void setTarget(MachineServer target) {
        this.target = target;
    }

    public SegmentServer getSegmentServer() {
        return segmentServer;
    }

    public void setSegmentServer(SegmentServer segmentServer) {
        this.segmentServer = segmentServer;
    }
}
