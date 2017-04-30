package mnix.mobilecloud.domain.client;

import com.orm.SugarRecord;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.util.Map;

public class SegmentClient extends SugarRecord {
    private String identifier;
    private String fileIdentifier;
    private Long byteFrom;
    private Long byteTo;
    private byte[] data;

    public SegmentClient() {
    }

    public SegmentClient(Map<String, String> params,
                         FileItemStream item) {
        this.setIdentifier(params.get("qquuid") + "_" + (params.containsKey("qqpartindex") ? params.get("qqpartindex") : 0));
        this.setFileIdentifier(params.get("qquuid"));
        Integer size = Integer.parseInt((params.containsKey("qqchunksize") ? params.get("qqchunksize") : params.get("qqtotalfilesize")));
        Integer fromByte = Integer.parseInt((params.containsKey("qqpartbyteoffset") ? params.get("qqpartbyteoffset") : "0"));
        this.setByteFrom(fromByte.longValue());
        this.setByteTo((long) fromByte + size);
//
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream(size);
        try {
            Streams.copy(item.openStream(), dataStream, true);
//            item.openStream().read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = dataStream.toByteArray();
        this.setData(data);
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
