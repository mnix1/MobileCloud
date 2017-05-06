package mnix.mobilecloud.domain.server;

import com.orm.SugarRecord;

import java.util.Map;

import mnix.mobilecloud.util.Util;

public class SegmentServer extends SugarRecord {
    protected String identifier;
    protected String fileIdentifier;
    protected String machineIdentifier;
    protected Long byteFrom;
    protected Long byteTo;

    public SegmentServer() {
    }

    public SegmentServer(Map<String, String> params) {
        String qquuid = Util.cutUuid(params.get("qquuid"));
        this.setIdentifier(qquuid + "_0_" + (params.containsKey("qqpartindex") ? params.get("qqpartindex") : 0));
        this.setFileIdentifier(qquuid);
        Integer size = Integer.parseInt((params.containsKey("qqchunksize") ? params.get("qqchunksize") : params.get("qqtotalfilesize")));
        Integer fromByte = Integer.parseInt((params.containsKey("qqpartbyteoffset") ? params.get("qqpartbyteoffset") : "0"));
        this.setByteFrom(fromByte.longValue());
        this.setByteTo((long) fromByte + size);
    }

    public SegmentServer(SegmentServer segmentServer) {
        this.identifier = segmentServer.getIdentifier();
        this.fileIdentifier = segmentServer.getFileIdentifier();
        this.machineIdentifier = segmentServer.getMachineIdentifier();
        this.byteFrom = segmentServer.getByteFrom();
        this.byteTo = segmentServer.getByteTo();
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

    public Long getSize() {
        return byteTo - byteFrom;
    }

    @Override
    public String toString() {
        return "SegmentServer{" +
                "identifier='" + identifier + '\'' +
                ", fileIdentifier='" + fileIdentifier + '\'' +
                ", machineIdentifier='" + machineIdentifier + '\'' +
                ", byteFrom=" + byteFrom +
                ", byteTo=" + byteTo +
                '}';
    }

}
