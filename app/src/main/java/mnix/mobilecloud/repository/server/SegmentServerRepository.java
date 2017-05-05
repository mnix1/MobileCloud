package mnix.mobilecloud.repository.server;


import android.text.TextUtils;

import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.util.Util;

public class SegmentServerRepository {
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

    public static List<SegmentServer> findByMachineIdentifier(String identifier) {
        return SegmentServer.find(SegmentServer.class, "machine_identifier = ?", identifier);
    }

    public static List<SegmentServer> findByMachineIdentifierAndFileIdentifier(String machineIdentifier, String fileIdentifier) {
        return SegmentServer.find(SegmentServer.class, "machine_identifier = ? AND file_identifier = ?", machineIdentifier, fileIdentifier);
    }

    public static List<SegmentServer> findByIdentifiers(List<String> identifiers) {
        return SegmentServer.find(SegmentServer.class, "identifier IN ('" + TextUtils.join("','", identifiers) + "')");
    }

    public static long getUsedSpace(String machineIdentifier) {
        List<SegmentServer> segmentServers = findByMachineIdentifier(machineIdentifier);
        long usedSpace = 0;
        for (SegmentServer segmentServer : segmentServers) {
            usedSpace += segmentServer.getSize();
        }
        return usedSpace;
    }


    public static long getUsedSpace(String machineIdentifier, String fileIdentifier) {
        List<SegmentServer> segmentServers = findByMachineIdentifierAndFileIdentifier(machineIdentifier, fileIdentifier);
        long usedSpace = 0;
        for (SegmentServer segmentServer : segmentServers) {
            usedSpace += segmentServer.getSize();
        }
        return usedSpace;
    }

    public static List<SegmentServer> list() {
        return SegmentServer.listAll(SegmentServer.class);
    }
}
