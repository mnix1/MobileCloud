package mnix.mobilecloud.algorithm.upload;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        int modulo = Option.getInstance().getDhtModulo();
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        int segmentKey = cutBytes(toSHA1(segmentServer.getIdentifier()), modulo);
//        Map<String, Integer> machineKeyMap = new HashMap<>();
        MachineServer targetMachine = null;
        int targetMachineKey = -1;
        for (MachineServer machineServer : machineServers) {
            int machineKey = cutBytes(toSHA1(machineServer.getIdentifier()), modulo);
//            machineKeyMap.put(machineServer.getIdentifier(), machineKey);
            if (targetMachineKey == -1 || (segmentKey <= machineKey && targetMachineKey > machineKey)) {
                targetMachineKey = machineKey;
                targetMachine = machineServer;
            }
        }
        return targetMachine;
    }

    @Override
    public List<MachineServer> getReplicaMachines(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        return null;
    }

    @Override
    public MachineServer getReplicaMachine(SegmentServer segmentServer, List<MachineServer> possibleMachines) {
        return null;
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
