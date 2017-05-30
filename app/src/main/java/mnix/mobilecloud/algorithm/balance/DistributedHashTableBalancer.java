package mnix.mobilecloud.algorithm.balance;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mnix.mobilecloud.algorithm.upload.DistributedHashTable;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.dto.SegmentMove;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.repository.server.SegmentServerRepository;

public class DistributedHashTableBalancer extends BalancePolicy {

    public int start(Context context) {
        List<SegmentMove> segmentMoves = findSegmentMoves();
        moveSegments(context, segmentMoves);
        return segmentMoves.size();
    }

    private List<SegmentMove> findSegmentMoves() {
        DistributedHashTable distributedHashTable = new DistributedHashTable();
        List<SegmentMove> segmentMoves = new ArrayList<>();
        Set<String> activeMachineIdentifiers = MachineServerRepository.findByActiveIdentifierSet(true);
        List<SegmentServer> allSegmentServers = SegmentServerRepository.findByMachineIdentifiers(activeMachineIdentifiers);
        Set<String> processedSegments = new HashSet<>();
        for (SegmentServer oneSegmentServer : allSegmentServers) {
            if (processedSegments.contains(oneSegmentServer.getIdentifier())) {
                continue;
            }
            List<SegmentServer> segmentServers = SegmentServerRepository.findByFileIdentifierAndByteFromOrderById(activeMachineIdentifiers, oneSegmentServer.getFileIdentifier(), oneSegmentServer.getByteFrom());
            List<String> usedMachineIdentifiers = new ArrayList<>(segmentServers.size());
            for (SegmentServer segmentServer : segmentServers) {
                processedSegments.add(segmentServer.getIdentifier());
                usedMachineIdentifiers.add(segmentServer.getMachineIdentifier());
            }
            for (SegmentServer segmentServer : segmentServers) {
                MachineServer sourceMachine = MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier());
                List<MachineServer> possibleMachines = MachineServerRepository.findByActiveAndNotIdentifiers(true, usedMachineIdentifiers);
                possibleMachines.add(sourceMachine);
                MachineServer destinationMachine = distributedHashTable.getMachine(segmentServer, possibleMachines);
                if (!sourceMachine.getIdentifier().equals(destinationMachine.getIdentifier())) {
                    segmentMoves.add(new SegmentMove(sourceMachine, destinationMachine, segmentServer));
                }
            }
        }
        return segmentMoves;
    }
}
