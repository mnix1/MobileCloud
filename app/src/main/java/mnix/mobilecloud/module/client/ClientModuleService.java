package mnix.mobilecloud.module.client;

import java.util.List;

import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.repository.client.SegmentClientRepository;
import mnix.mobilecloud.util.BinarySearcher;

public class ClientModuleService {
    public static List<Integer> index(String identifier, byte[] countData) {
        SegmentClient segmentClient = SegmentClientRepository.findByIdentifier(identifier);
        BinarySearcher bs = new BinarySearcher();
        return bs.searchBytes(segmentClient.getData(), countData);
    }

    public static int count(List<String> identifiers, byte[] countData) {
        List<SegmentClient> segmentClients = SegmentClientRepository.findByIdentifiers(identifiers);
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
