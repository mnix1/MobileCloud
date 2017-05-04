package mnix.mobilecloud.repository.client;


import android.text.TextUtils;

import org.apache.commons.fileupload.FileItemStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.dto.SegmentClientDTO;

public class SegmentClientRepository {
    public static void save(Map<String, String> params,
                            FileItemStream item) {
        SegmentClient segmentClient = new SegmentClient(params, item);
        segmentClient.save();
    }

    public static List<SegmentClientDTO> list() {
        List<SegmentClientDTO> dtos = new ArrayList<>();
        Iterator<SegmentClient> segmentClientIterator = SegmentClient.findAsIterator(SegmentClient.class, "1=1");
        while (segmentClientIterator.hasNext()) {
            dtos.add(new SegmentClientDTO(segmentClientIterator.next()));
        }
        return dtos;
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

    public static List<SegmentClient> findByFileIdentifier(String identifier) {
        return SegmentClient.find(SegmentClient.class, "file_identifier = ?", new String[]{identifier}, null, "byte_from", null);
    }

    public static List<SegmentClient> findByIdentifiers(List<String> identifiers) {
        return SegmentClient.find(SegmentClient.class, "identifier IN ('" + TextUtils.join("','", identifiers) + "')");
    }
}
