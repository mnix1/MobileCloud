package mnix.mobilecloud.repository.server;


import java.util.List;

import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.util.Util;

public class MachineServerRepository {
    public static void update(MachineServer machineServer) {
        Util.log(MachineServerRepository.class, "update", machineServer.toString());
        MachineServer currentMachineServer = findByIdentifier(machineServer.getIdentifier());
        if (currentMachineServer == null) {
            Util.log(MachineServerRepository.class, "update", "save");
            machineServer.save();
        } else {
            Util.log(MachineServerRepository.class, "update", "update");
            currentMachineServer.setRole(machineServer.getRole());
            currentMachineServer.setIpAddress(machineServer.getIpAddress());
            currentMachineServer.setLastContact(machineServer.getLastContact());
            currentMachineServer.save();
        }
    }

    public static MachineServer findByIdentifier(String identifier) {
        Util.log(MachineServerRepository.class, "findByIdentifier", identifier);
        List<MachineServer> machineServers = MachineServer.find(MachineServer.class, "identifier = ?", identifier);
        if (machineServers.size() > 1) {
            Util.log(MachineServerRepository.class, "findByIdentifier", "not unique");
            throw new IndexOutOfBoundsException("Invalid identifier (not unique)");
        } else if (machineServers.size() == 0) {
            return null;
        }
        MachineServer machineServer = machineServers.get(0);
        Util.log(MachineServerRepository.class, "findByIdentifier", machineServer.toString());
        return machineServer;
    }

    public static List<MachineServer> findByRole(MachineRole role) {
        return MachineServer.find(MachineServer.class, "role = ?", role.toString());
    }

    public static List<MachineServer> list() {
        return MachineServer.listAll(MachineServer.class);
    }
}
