package mnix.mobilecloud.domain.client;

import com.orm.SugarRecord;

public class SegmentClient extends SugarRecord {
    private String identifier;
    private String fileIdentifier;
    private Long byteFrom;
    private Long byteTo;
    private byte[] data;

    public SegmentClient() {
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
