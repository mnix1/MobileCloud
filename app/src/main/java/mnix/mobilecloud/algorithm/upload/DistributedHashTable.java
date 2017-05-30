package mnix.mobilecloud.algorithm.upload;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.MachineServerRepository;

public class DistributedHashTable extends UploadPolicy {
    @Override
    public MachineServer getMachine(SegmentServer segmentServer) {
        return getMachine(segmentServer, MachineServerRepository.findByActive(true));
    }

    public MachineServer getMachine(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        int modulo = Option.getInstance().getDhtModulo();
        int segmentKey = cutBytes(toSHA1(segmentServer.getIdentifier()), modulo);
        List<Integer> machineKeys = new ArrayList<>();
        Map<Integer, MachineServer> machineMap = new HashMap<>();
        for (MachineServer machineServer : possibleMachines) {
            int machineKey = cutBytes(toSHA1(machineServer.getIdentifier()), modulo);
            machineKeys.add(machineKey);
            machineMap.put(machineKey, machineServer);
        }
        Collections.sort(machineKeys);
        for (Integer machineKey : machineKeys) {
            if (segmentKey <= machineKey) {
                return machineMap.get(machineKey);
            }
        }
        return machineMap.get(machineKeys.get(0));
    }

    @Override
    public List<MachineServer> getReplicaMachines(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        return null;
    }

    @Override
    public MachineServer getReplicaMachine(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        return getMachine(segmentServer, possibleMachines);
    }

    public static byte[] toSHA1(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md.digest(bytes);
    }

    public static byte[] toSHA1(String string) {
        try {
            return toSHA1(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int cutBytes(byte[] bytes, int modulo) {
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result *= (256 % modulo);
            result %= modulo;
            result += ((bytes[i] + 128) % modulo);
            result %= modulo;
        }
        return result;
    }
}
