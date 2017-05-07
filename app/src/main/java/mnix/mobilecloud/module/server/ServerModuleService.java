package mnix.mobilecloud.module.server;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.communication.server.ServerModuleCommunication;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.module.ModuleError;
import mnix.mobilecloud.module.client.ClientModuleService;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.util.Util;
import rx.Observable;
import rx.functions.Func2;

import static mnix.mobilecloud.network.NetworkUtil.getIpAddress;

public class ServerModuleService {
    public static int count(final List<SegmentServer> segmentServers, byte[] countData, ServerModuleCommunication moduleCommunication) throws ModuleError {
        Map<String, List<String>> machineGroupedSegmentIdentifiers = new HashMap<>();
        for (SegmentServer segmentServer : segmentServers) {
            if (machineGroupedSegmentIdentifiers.containsKey(segmentServer.getMachineIdentifier())) {
                machineGroupedSegmentIdentifiers.get(segmentServer.getMachineIdentifier()).add(segmentServer.getIdentifier());
            } else {
                List<String> list = new ArrayList<>();
                list.add(segmentServer.getIdentifier());
                machineGroupedSegmentIdentifiers.put(segmentServer.getMachineIdentifier(), list);
            }
        }

        String byteParam = byteParam(countData);
        //TODO Possible BUG!!!
        Observable<Integer> observableResult = Observable.just(0);
        for (String machineIdentifier : machineGroupedSegmentIdentifiers.keySet()) {
            MachineServer machineServer = MachineServerRepository.findByIdentifier(machineIdentifier);
            List<String> segmentIdentifiers = machineGroupedSegmentIdentifiers.get(machineIdentifier);
//            if (machineServer.isMaster()) {
//                result += ClientModuleService.count(segmentIdentifiers, countData);
//                continue;
//            }
            String identifierParam = TextUtils.join("&identifier=", segmentIdentifiers);
            Observable<Integer> localObservable = moduleCommunication.count(machineServer.isMaster() ? getIpAddress() : machineServer.getIpAddress(), "identifier=" + identifierParam + "&byte=" + byteParam);
            observableResult = observableResult.mergeWith(localObservable);
        }
        int result = observableResult.reduce(0, new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer all, Integer val) {
                Util.log(this.getClass(), "count", "all: " + all + ", val: " + val);
                return all + val;
            }
        }).toBlocking().first();
        return result;
    }
//    public static int count(final List<SegmentServer> segmentServers, byte[] countData, ServerModuleCommunication moduleCommunication) throws ModuleError {
//        String byteParam = byteParam(countData);
//        int result = 0;
//        Observable<Integer> observableResult = Observable.just(0);
//        for (SegmentServer segmentServer : segmentServers) {
//            MachineServer machineServer = MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier());
//            List<String> segmentIdentifiers = new ArrayList<>();
//            segmentIdentifiers.add(segmentServer.getIdentifier());
//            if (machineServer.isMaster()) {
//                result += ClientModuleService.count(segmentIdentifiers, countData);
//                continue;
//            }
//            String identifierParam = TextUtils.join("&identifier=", segmentIdentifiers);
//            Observable<Integer> localObservable = moduleCommunication.count(machineServer, "?identifier=" + identifierParam + "&byte=" + byteParam);
//            observableResult = observableResult.mergeWith(localObservable);
//        }
//        result = observableResult.reduce(result, new Func2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer all, Integer val) {
//                Util.log(this.getClass(), "count", "all: " + all + ", val: " + val);
//                return all + val;
//            }
//        }).toBlocking().first();
//        return result;
//    }

    private static String byteParam(byte[] data) {
        List<String> stringBytes = new ArrayList<>(data.length);
        for (byte b : data) {
            stringBytes.add(Byte.toString(b));
        }
        return TextUtils.join(",", stringBytes);
    }
}
