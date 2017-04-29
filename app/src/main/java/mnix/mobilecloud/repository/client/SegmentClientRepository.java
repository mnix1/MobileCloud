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
        SegmentClient segmentClient = new SegmentClient(params, item);
        segmentClient.save();
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
