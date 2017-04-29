package mnix.mobilecloud.repository.client;


import android.util.Log;

import java.util.UUID;

import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.domain.client.MachineClient;

public class MachineClientRepository {
    public static MachineClient get() {
        return MachineClient.first(MachineClient.class);
    }

    public static void setUniqueIdentifier() {
        Log.e("MOBILE CLOUD", "setUniqueIdentifier");
        MachineClient machineClient = get();
        String identifier = machineClient != null ? machineClient.getIdentifier() : null;
        if (identifier == null) {
            Log.e("MOBILE CLOUD", "identifier: " + identifier);
            identifier = UUID.randomUUID().toString();
            machineClient = new MachineClient();
            machineClient.setIdentifier(identifier);
            machineClient.save();
        }
    }

    public static void updateRole(MachineRole machineRole) {
        Log.e("MOBILE CLOUD", "updateRole");
        MachineClient machineClient = get();
        machineClient.setRole(machineRole);
        machineClient.update();
    }

    public static boolean isServer() {
        return get().getRole() == MachineRole.MASTER;
    }
}
