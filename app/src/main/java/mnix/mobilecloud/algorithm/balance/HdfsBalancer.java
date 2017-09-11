package mnix.mobilecloud.algorithm.balance;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.dto.MachineInformationDTO;
import mnix.mobilecloud.dto.SegmentMove;
import mnix.mobilecloud.option.Option;
import mnix.mobilecloud.repository.server.MachineServerRepository;

public class HdfsBalancer extends BalancePolicy {
    private final List<MachineInformationDTO> overUtilized = new ArrayList<>();
    private final List<MachineInformationDTO> aboveAverage = new ArrayList<>();
    private final List<MachineInformationDTO> belowAverage = new ArrayList<>();
    private final List<MachineInformationDTO> underUtilized = new ArrayList<>();

    private final Map<MachineInformationDTO, MachineInformationDTO> pairOverUtilizedUnderUtilized = new HashMap<>();
    private final Map<MachineInformationDTO, MachineInformationDTO> pairOverUtilizedBelowAverage = new HashMap<>();
    private final Map<MachineInformationDTO, MachineInformationDTO> pairAboveAverageUnderUtilized = new HashMap<>();

    public int start(Context context) {
        int steps = iteration(context);
        int totalSteps = steps;
        while (steps > 0) {
            clear();
            steps = iteration(context);
            totalSteps += steps;
        }
        return totalSteps;
    }

    public void clear(){
        overUtilized.clear();
        aboveAverage.clear();
        belowAverage.clear();
        underUtilized.clear();
        pairOverUtilizedUnderUtilized.clear();
        pairOverUtilizedBelowAverage.clear();
        pairAboveAverageUnderUtilized.clear();
    }

    public int iteration(Context context) {
        List<MachineServer> machineServers = MachineServerRepository.findByActive(true);
        List<MachineInformationDTO> machineInformationList = MachineServerRepository.prepareInformation(machineServers);
        classify(machineServers, machineInformationList);
        if (overUtilized.size() + underUtilized.size() == 0) {
            return 0;
        }
        pairGroups();
        List<SegmentMove> segmentMoves = findSegmentsToMove();
        moveSegments(context, segmentMoves);
        return segmentMoves.size();
    }

    private void classify(List<MachineServer> machineServers, List<MachineInformationDTO> machineInformationList) {
        double avgUsedSpacePercent = MachineInformationDTO.getUsedSpacePercent(MachineServerRepository.calculateTotalCapacity(machineServers),
                MachineServerRepository.calculateTotalUsedSpace(machineInformationList));
        double thresholdPercent = Option.getInstance().getUtilizationThreshold() * 100;
        for (MachineInformationDTO machineInformationDTO : machineInformationList) {
            double machineUsedSpacePercent = machineInformationDTO.getUsedSpacePercent();
            if (machineUsedSpacePercent > avgUsedSpacePercent + thresholdPercent) {
                overUtilized.add(machineInformationDTO);
            } else if (machineUsedSpacePercent > avgUsedSpacePercent && machineUsedSpacePercent <= avgUsedSpacePercent + thresholdPercent) {
                aboveAverage.add(machineInformationDTO);
            } else if (machineUsedSpacePercent >= avgUsedSpacePercent - thresholdPercent && machineUsedSpacePercent <= avgUsedSpacePercent) {
                belowAverage.add(machineInformationDTO);
            } else if (machineUsedSpacePercent < avgUsedSpacePercent - thresholdPercent) {
                underUtilized.add(machineInformationDTO);
            }
        }
    }

    private void pairGroups() {
        sortGroups();
        while (overUtilized.size() > 0 && underUtilized.size() > 0) {
            MachineInformationDTO source = overUtilized.get(0);
            MachineInformationDTO target = underUtilized.get(0);
            pairOverUtilizedUnderUtilized.put(source, target);
            overUtilized.remove(0);
            underUtilized.remove(0);
        }
        while (overUtilized.size() > 0 && belowAverage.size() > 0) {
            MachineInformationDTO source = overUtilized.get(0);
            MachineInformationDTO target = belowAverage.get(0);
            pairOverUtilizedBelowAverage.put(source, target);
            overUtilized.remove(0);
            belowAverage.remove(0);
        }
        while (aboveAverage.size() > 0 && underUtilized.size() > 0) {
            MachineInformationDTO source = aboveAverage.get(0);
            MachineInformationDTO target = underUtilized.get(0);
            pairAboveAverageUnderUtilized.put(source, target);
            aboveAverage.remove(0);
            underUtilized.remove(0);
        }
    }

    private void sortGroups() {
        Collections.sort(overUtilized, new Comparator<MachineInformationDTO>() {
            @Override
            public int compare(MachineInformationDTO o1, MachineInformationDTO o2) {
                return Double.compare(o1.getUsedSpacePercent(), o2.getUsedSpacePercent());
            }
        });
        Collections.sort(aboveAverage, new Comparator<MachineInformationDTO>() {
            @Override
            public int compare(MachineInformationDTO o1, MachineInformationDTO o2) {
                return Double.compare(o1.getUsedSpacePercent(), o2.getUsedSpacePercent());
            }
        });
        Collections.sort(belowAverage, new Comparator<MachineInformationDTO>() {
            @Override
            public int compare(MachineInformationDTO o1, MachineInformationDTO o2) {
                return Double.compare(o2.getUsedSpacePercent(), o1.getUsedSpacePercent());
            }
        });
        Collections.sort(underUtilized, new Comparator<MachineInformationDTO>() {
            @Override
            public int compare(MachineInformationDTO o1, MachineInformationDTO o2) {
                return Double.compare(o2.getUsedSpacePercent(), o1.getUsedSpacePercent());
            }
        });
    }

    private List<SegmentMove> findSegmentsToMove() {
        List<SegmentMove> segmentMoves = new ArrayList<>();
        for (MachineInformationDTO machineInformationDTO : pairOverUtilizedUnderUtilized.keySet()) {
            SegmentMove segmentMove = findSegmentToMove(machineInformationDTO, pairOverUtilizedUnderUtilized.get(machineInformationDTO));
            if (segmentMove != null) {
                segmentMoves.add(segmentMove);
            }
        }
        for (MachineInformationDTO machineInformationDTO : pairOverUtilizedBelowAverage.keySet()) {
            SegmentMove segmentMove = findSegmentToMove(machineInformationDTO, pairOverUtilizedBelowAverage.get(machineInformationDTO));
            if (segmentMove != null) {
                segmentMoves.add(segmentMove);
            }
        }
        for (MachineInformationDTO machineInformationDTO : pairAboveAverageUnderUtilized.keySet()) {
            SegmentMove segmentMove = findSegmentToMove(machineInformationDTO, pairAboveAverageUnderUtilized.get(machineInformationDTO));
            if (segmentMove != null) {
                segmentMoves.add(segmentMove);
            }
        }
        return segmentMoves;
    }

    private SegmentMove findSegmentToMove(MachineInformationDTO source, MachineInformationDTO target) {
        Set<String> targetSegments = new HashSet<>();
        for (SegmentServer segmentServer : target.getSegmentServers()) {
            targetSegments.add(segmentServer.getFileIdentifier() + segmentServer.getByteFrom());
        }
        for (SegmentServer segmentServer : source.getSegmentServers()) {
            if (!targetSegments.contains(segmentServer.getFileIdentifier() + segmentServer.getByteFrom())) {
                return new SegmentMove(source.getMachineServer(), target.getMachineServer(), segmentServer);
            }
        }
        return null;
    }

}
