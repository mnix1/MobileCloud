package mnix.mobilecloud.dto;

import java.util.List;

import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;

public class StateDTO {
    List<FileServer> file;
    List<MachineServer> machine;
    List<SegmentServer> segment;

    public List<FileServer> getFile() {
        return file;
    }

    public void setFile(List<FileServer> file) {
        this.file = file;
    }

    public List<MachineServer> getMachine() {
        return machine;
    }

    public void setMachine(List<MachineServer> machine) {
        this.machine = machine;
    }

    public List<SegmentServer> getSegment() {
        return segment;
    }

    public void setSegment(List<SegmentServer> segment) {
        this.segment = segment;
    }
}
