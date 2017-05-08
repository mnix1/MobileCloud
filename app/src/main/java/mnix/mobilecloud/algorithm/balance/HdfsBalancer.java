package mnix.mobilecloud.algorithm.balance;

import android.content.Context;

import java.util.List;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.dto.MachineInformationDTO;
import mnix.mobilecloud.repository.server.MachineServerRepository;

public class HdfsBalancer extends BalancePolicy {
    public int start(Context context) {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        List<MachineInformationDTO> machineInformationList = MachineServerRepository.prepareInformation(machineServers);
        double totalUsedSpacePercent = MachineInformationDTO.getUsedSpacePercent(MachineServerRepository.calculateTotalFreeSpace(machineServers),
                MachineServerRepository.calculateTotalUsedSpace(machineInformationList));
        double avgUsedSpacePercent = totalUsedSpacePercent / machineServers.size();
        return 0;
    }


}
