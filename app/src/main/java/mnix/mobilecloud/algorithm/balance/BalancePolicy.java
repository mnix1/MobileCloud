package mnix.mobilecloud.algorithm.balance;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnix.mobilecloud.communication.server.SegmentServerCommunication;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.dto.SegmentMove;
import mnix.mobilecloud.network.NetworkUtil;
import mnix.mobilecloud.web.client.SegmentClientController;

public abstract class BalancePolicy {

    public abstract int start(Context context);


    protected void moveSegments(Context context, List<SegmentMove> segmentMoves) {
        SegmentServerCommunication segmentCommunication = new SegmentServerCommunication(context);
        for (SegmentMove segmentMove : segmentMoves) {
            MachineServer sourceMachine = segmentMove.getSource();
            MachineServer destinationMachine = segmentMove.getTarget();
            SegmentServer segmentServer = segmentMove.getSegmentServer();
            if (sourceMachine.isMaster()) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("identifier", segmentServer.getIdentifier());
                params.put("newIdentifier", segmentServer.getIdentifier());
                params.put("address", destinationMachine.getIpAddress());
                SegmentClientController.processSend(params, context);
            } else {
                segmentCommunication.sendSegment(segmentServer, sourceMachine.getIpAddress(),
                        destinationMachine.isMaster() ? NetworkUtil.getIpAddress() : destinationMachine.getIpAddress(), segmentServer.getIdentifier());
            }
            segmentCommunication.deleteSegment(segmentServer, sourceMachine.isMaster() ? NetworkUtil.getIpAddress() : sourceMachine.getIpAddress());
            segmentServer.delete();
        }
    }
}
