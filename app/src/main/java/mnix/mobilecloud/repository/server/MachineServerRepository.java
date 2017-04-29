package mnix.mobilecloud.repository.server;


import android.util.Log;

import java.util.List;

import mnix.mobilecloud.domain.server.MachineServer;

public class MachineServerRepository {
    public static void update(MachineServer machineServer) {
        Log.e("MobileCloud","MachineServerRepository update");
        MachineServer currentMachineServer = findByIdentifier(machineServer.getIdentifier());
        if (currentMachineServer == null) {
            Log.e("MobileCloud","MachineServerRepository update save");
            machineServer.save();
        } else {
            Log.e("MobileCloud","MachineServerRepository update update");
            currentMachineServer.setRole(machineServer.getRole());
            currentMachineServer.setIpAddress(machineServer.getIpAddress());
            currentMachineServer.setLastContact(machineServer.getLastContact());
            currentMachineServer.update();
        }
    }

    public static MachineServer findByIdentifier(String identifier) {
        List<MachineServer> machineServers = MachineServer.find(MachineServer.class, "identifier = ?", identifier);
        if (machineServers.size() > 1) {
            Log.e("MobileCloud","MachineServerRepository findByIdentifier not unique");
            throw new IndexOutOfBoundsException("Invalid identifier (not unique)");
        } else if (machineServers.size() == 0) {
            return null;
        }
        return machineServers.get(0);
    }

    public static List<MachineServer> list() {
        return MachineServer.listAll(MachineServer.class);
    }
}
