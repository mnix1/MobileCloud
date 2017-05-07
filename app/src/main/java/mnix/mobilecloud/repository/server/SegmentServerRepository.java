package mnix.mobilecloud.repository.server;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.web.WebServer;
import mnix.mobilecloud.web.socket.Action;

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
        return SegmentServer.find(SegmentServer.class, "file_identifier = ?", new String[]{identifier}, null, "byte_from, id", null);
    }

    public static List<SegmentServer> findActiveByFileIdentifierOrderById(String identifier) {
        Set<String> activeMachineIdentifiers = MachineServerRepository.findByActiveIdentifierSet(true);
        List<SegmentServer> result = new ArrayList<>();
        List<SegmentServer> segmentServers = findByFileIdentifier(identifier);
        Set<Long> byteFromSet = new HashSet<>();
        for (SegmentServer segmentServer : segmentServers) {
            if (activeMachineIdentifiers.contains(segmentServer.getMachineIdentifier()) && !byteFromSet.contains(segmentServer.getByteFrom())) {
                result.add(segmentServer);
                byteFromSet.add(segmentServer.getByteFrom());
            }
        }
        return result;
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

    public static boolean updateSegment(String segmentIdentifier, String machineIdentifier, WebServer webServer) {
        String[] splitIdentifier = segmentIdentifier.split("_");
        SegmentServer originalSegment = SegmentServerRepository.findByIdentifier(splitIdentifier[0] + "_0_" + splitIdentifier[2]);
        if (originalSegment == null) {
            return false;
        }
        SegmentServer newSegment = new SegmentServer(originalSegment);
        newSegment.setIdentifier(segmentIdentifier);
        newSegment.setMachineIdentifier(machineIdentifier);
        newSegment.save();
        webServer.sendWebSocketMessage(Action.SEGMENT_UPLOADED);
        return true;
    }
}
