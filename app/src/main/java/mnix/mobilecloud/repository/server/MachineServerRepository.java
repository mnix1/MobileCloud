package mnix.mobilecloud.repository.server;


import java.util.List;

import mnix.mobilecloud.domain.server.MachineServer;

public class MachineServerRepository {
    public static void update(MachineServer machineServer) {
        MachineServer currentMachineServer = findByIdentifier(machineServer.getIdentifier());
        if (currentMachineServer == null) {
            machineServer.save();
        } else {
            currentMachineServer.setRole(machineServer.getRole());
            currentMachineServer.setIpAddress(machineServer.getIpAddress());
            currentMachineServer.setLastContact(machineServer.getLastContact());
            currentMachineServer.update();
        }
    }

    public static MachineServer findByIdentifier(String identifier) {
        List<MachineServer> machineServers = MachineServer.find(MachineServer.class, "identifier = ?", identifier);
        if (machineServers.size() > 1) {
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
