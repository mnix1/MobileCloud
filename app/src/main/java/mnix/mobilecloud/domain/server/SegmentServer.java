package mnix.mobilecloud.domain.server;

import com.orm.SugarRecord;

public class SegmentServer extends SugarRecord {
    private String identifier;
    private String fileIdentifier;
    private String machineIdentifier;
    private Long byteFrom;
    private Long byteTo;

    public SegmentServer() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public String getMachineIdentifier() {
        return machineIdentifier;
    }

    public void setMachineIdentifier(String machineIdentifier) {
        this.machineIdentifier = machineIdentifier;
    }

    public Long getByteFrom() {
        return byteFrom;
    }

    public void setByteFrom(Long byteFrom) {
        this.byteFrom = byteFrom;
    }

    public Long getByteTo() {
        return byteTo;
    }

    public void setByteTo(Long byteTo) {
        this.byteTo = byteTo;
    }
}
