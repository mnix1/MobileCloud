package mnix.mobilecloud.domain.client;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.util.Map;

import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.util.Util;

public class SegmentClient extends SegmentServer {
    private byte[] data;

    public SegmentClient() {
    }

    public SegmentClient(SegmentServer segmentServer,
                         FileItemStream item) {
        this.identifier = segmentServer.getIdentifier();
        this.fileIdentifier = segmentServer.getFileIdentifier();
        this.machineIdentifier = segmentServer.getMachineIdentifier();
        this.byteFrom = segmentServer.getByteFrom();
        this.byteTo = segmentServer.getByteTo();
        setData(item);
    }

    public SegmentClient(Map<String, String> params,
                         FileItemStream item) {
        this.setIdentifier(params.get("qquuid"));
        this.setFileIdentifier(getIdentifier().split("_")[0]);
        Integer size = Integer.parseInt((params.containsKey("qqchunksize") ? params.get("qqchunksize") : params.get("qqtotalfilesize")));
        Integer fromByte = Integer.parseInt((params.containsKey("qqpartbyteoffset") ? params.get("qqpartbyteoffset") : "0"));
        this.setByteFrom(fromByte.longValue());
        this.setByteTo((long) fromByte + size);
        setData(item);
    }


    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setData(FileItemStream item) {
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream(getSize().intValue());
        try {
            Streams.copy(item.openStream(), dataStream, true);
//            item.openStream().read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = dataStream.toByteArray();
        this.setData(data);
    }
}
