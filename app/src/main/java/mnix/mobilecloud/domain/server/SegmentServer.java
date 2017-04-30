package mnix.mobilecloud.domain.server;

import com.orm.SugarRecord;

import java.util.Map;

public class SegmentServer extends SugarRecord {
    protected String identifier;
    protected String fileIdentifier;
    protected String machineIdentifier;
    protected Long byteFrom;
    protected Long byteTo;

    public SegmentServer() {
    }

    public SegmentServer(Map<String, String> params){
        this.setIdentifier(params.get("qquuid") + "_" + (params.containsKey("qqpartindex") ? params.get("qqpartindex") : 0));
        this.setFileIdentifier(params.get("qquuid"));
        Integer size = Integer.parseInt((params.containsKey("qqchunksize") ? params.get("qqchunksize") : params.get("qqtotalfilesize")));
        Integer fromByte = Integer.parseInt((params.containsKey("qqpartbyteoffset") ? params.get("qqpartbyteoffset") : "0"));
        this.setByteFrom(fromByte.longValue());
        this.setByteTo((long) fromByte + size);
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
