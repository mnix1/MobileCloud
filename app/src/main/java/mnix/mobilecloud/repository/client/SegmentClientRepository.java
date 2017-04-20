package mnix.mobilecloud.repository.client;


import org.apache.commons.fileupload.FileItemStream;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.dto.SegmentClientDTO;

public class SegmentClientRepository {
    public static void save(Map<String, String> params,
                            FileItemStream item) {
        SegmentClient segment = new SegmentClient();
        segment.setIdentifier(params.get("qquuid") + "_" + (params.containsKey("qqpartindex") ? params.get("qqpartindex") : 0));
        segment.setFileIdentifier(params.get("qquuid"));
        Integer size = Integer.parseInt((params.containsKey("qqchunksize") ? params.get("qqchunksize") : params.get("qqtotalfilesize")));
        Integer fromByte = Integer.parseInt((params.containsKey("qqpartbyteoffset") ? params.get("qqpartbyteoffset") : "0"));
        segment.setByteFrom(fromByte.longValue());
        segment.setByteTo((long) fromByte + size);
        byte[] data = new byte[size];
        try {
            item.openStream().read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        segment.setData(data);
        segment.save();
    }

    public static List<SegmentClientDTO> list() {
        return Observable.fromIterable(SegmentClient.listAll(SegmentClient.class)).map(new Function<SegmentClient, SegmentClientDTO>() {
            @Override
            public SegmentClientDTO apply(@NonNull SegmentClient segmentClient) throws Exception {
                return new SegmentClientDTO(segmentClient);
            }
        }).toList().blockingGet();
    }

    public static SegmentClient findByIdentifier(String identifier) {
        List<SegmentClient> segmentClients = SegmentClient.find(SegmentClient.class, "identifier = ?", identifier);
        if (segmentClients.size() > 1) {
            throw new IndexOutOfBoundsException("Invalid identifier (not unique)");
        } else if (segmentClients.size() == 0) {
            return null;
        }
        return segmentClients.get(0);
    }
}
