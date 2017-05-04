package mnix.mobilecloud.repository.client;


import java.util.UUID;

import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.domain.client.MachineClient;
import mnix.mobilecloud.util.Util;

public class MachineClientRepository {
    public static MachineClient get() {
        return MachineClient.first(MachineClient.class);
    }

    public static void update() {
        Util.log(MachineClientRepository.class, "update");
        MachineClient machineClient = get();
        String identifier = machineClient != null ? machineClient.getIdentifier() : null;
        if (identifier == null) {
            identifier = UUID.randomUUID().toString();
            identifier = Util.cutUuid(identifier);
//            identifier = Util.getId();
            Util.log(MachineClientRepository.class, "update", "identifier: " + identifier);
            machineClient = new MachineClient();
            machineClient.setIdentifier(identifier);
            machineClient.setDevice(Util.getDeviceName());
            machineClient.setSystem(Util.getSystem());
            machineClient.setName(Util.getName());
        }
        machineClient.setSpeed(Util.calculateSpeed());
        machineClient.setSpace(Util.getSpace());
        machineClient.save();
    }

    public static void updateRole(MachineRole machineRole) {
        Util.log(MachineClientRepository.class, "updateRole", machineRole.toString());
        MachineClient machineClient = get();
        machineClient.setRole(machineRole);
        machineClient.save();
    }

    public static boolean isServer() {
        return get().getRole() == MachineRole.MASTER;
    }
}
