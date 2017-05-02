package mnix.mobilecloud.repository.client;


import android.text.TextUtils;

import org.apache.commons.fileupload.FileItemStream;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.dto.SegmentClientDTO;
import mnix.mobilecloud.util.BinarySearcher;

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

    public static List<SegmentClient> findByIdentifiers(List<String> identifiers) {
        return SegmentClient.find(SegmentClient.class, "identifier IN (?)", TextUtils.join(",", identifiers));
    }

    public static List<Integer> index(String identifier, byte[] countData) {
        SegmentClient segmentClient = findByIdentifier(identifier);
        BinarySearcher bs = new BinarySearcher();
        return bs.searchBytes(segmentClient.getData(), countData);
    }

    public static int count(List<String> identifiers, byte[] countData) {
        List<SegmentClient> segmentClients = findByIdentifiers(identifiers);
        int result = 0;
        BinarySearcher bs = new BinarySearcher();
        for (SegmentClient segmentClient : segmentClients) {
            byte[] data = segmentClient.getData();
            List<Integer> indexes = bs.searchBytes(data, countData);
            result += indexes.size();
        }
        return result;
    }


}
