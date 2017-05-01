package mnix.mobilecloud.repository.server;


import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.util.Util;

public class SegmentServerRepository {
    public static void save(Map<String, String> params) {
        SegmentServer segmentServer = new SegmentServer();
        String qquuid = Util.cutUuid(params.get("qquuid"));
        segmentServer.setIdentifier(qquuid + "_" + (params.containsKey("qqpartindex") ? params.get("qqpartindex") : 0));
        segmentServer.setFileIdentifier(qquuid);
        Integer size = Integer.parseInt((params.containsKey("qqchunksize") ? params.get("qqchunksize") : params.get("qqtotalfilesize")));
        Integer fromByte = Integer.parseInt((params.containsKey("qqpartbyteoffset") ? params.get("qqpartbyteoffset") : "0"));
        segmentServer.setByteFrom(fromByte.longValue());
        segmentServer.setByteTo((long) fromByte + size);
        segmentServer.save();
    }

    public static SegmentServer findByIdentifier(String identifier) {
        List<SegmentServer> segmentServers = SegmentServer.find(SegmentServer.class, "identifier = ?", identifier);
        if (segmentServers.size() > 1) {
            throw new IndexOutOfBoundsException("Invalid identifier (not unique)");
        } else if (segmentServers.size() == 0) {
            return null;
        }
        return segmentServers.get(0);
    }

    public static List<SegmentServer> findByFileIdentifier(String identifier) {
        return SegmentServer.find(SegmentServer.class, "file_identifier = ?", new String[]{identifier}, null, "byte_from", null);
    }

    public static List<SegmentServer> list() {
        return SegmentServer.listAll(SegmentServer.class);
    }
}
