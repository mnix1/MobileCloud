package org.nanohttpd.protocols.http.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mnix.mobilecloud.communication.server.SegmentServerCommunication;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.repository.client.SegmentClientRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;

public class StreamingResponse extends Response {
    private List<SegmentServer> segmentServers;
    private SegmentServerCommunication segmentCommunication;

    public StreamingResponse(IStatus status, String mimeType, SegmentServerCommunication segmentCommunication, long size, List<SegmentServer> segmentServers) {
        super(status, mimeType, null, 0);
        this.contentLength = size;
        this.segmentCommunication = segmentCommunication;
        this.segmentServers = segmentServers;
    }

    public static class StreamingResponseWrapper {
        OutputStream outputStream;
        int written;

        StreamingResponseWrapper(OutputStream outputStream) {
            this.outputStream = outputStream;
            this.written = 0;
        }

        public void sent(long sent) {
            written += sent;
        }

        public OutputStream getOutputStream() {
            return outputStream;
        }

        public int getWritten() {
            return written;
        }
    }

    protected void sendBody(OutputStream outputStream, long pending) throws IOException {
        StreamingResponseWrapper wrapper = new StreamingResponseWrapper(outputStream);
        int segmentsWritten = 0;
        for (SegmentServer segmentServer : segmentServers) {
            MachineServer machineServer = MachineServerRepository.findByIdentifier(segmentServer.getMachineIdentifier());
            if (machineServer.isMaster()) {
                SegmentClient segmentClient = SegmentClientRepository.findByIdentifier(segmentServer.getIdentifier());
                ByteBuf byteBuf = Unpooled.copiedBuffer(segmentClient.getData());
//                Util.log(this.getClass(), "sendBody", "byteBuf: " + byteBuf.readableBytes());
                wrapper.sent(byteBuf.readableBytes());
                byteBuf.readBytes(wrapper.getOutputStream(), byteBuf.readableBytes());
            } else {
                segmentCommunication.downloadSegment(segmentServer, machineServer.getIpAddress(), wrapper);
                while (wrapper.getWritten() < segmentServer.getSize() + segmentsWritten) {
//                    Util.log(this.getClass(), "sendBody", "getWritten: " + wrapper.getWritten() +
//                            " segmentServer.getSize(): " + segmentServer.getSize());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            segmentsWritten += segmentServer.getSize();
        }
        if (this.data != null) {
            this.data.close();
        }
    }
}