package org.nanohttpd.protocols.http.response;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;

import mnix.mobilecloud.communication.server.ServerSegmentCommunication;
import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.util.Util;

public class StreamingResponse extends Response {
    private List<SegmentServer> segmentServers;
    private FileServer fileServer;
    private ServerSegmentCommunication segmentCommunication;

    public StreamingResponse(IStatus status, String mimeType, ServerSegmentCommunication segmentCommunication, FileServer fileServer, List<SegmentServer> segmentServers) {
        super(status, mimeType, null, 0);
        this.contentLength = fileServer.getSize();
        this.segmentCommunication = segmentCommunication;
        this.fileServer = fileServer;
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
            segmentCommunication.downloadSegment(segmentServer, MachineServerRepository.findByIdentifier(segmentServers.get(0).getMachineIdentifier()), wrapper);
            while (wrapper.getWritten() < segmentServer.getSize() + segmentsWritten) {
                Util.log(this.getClass(), "sendBody", "getWritten: " + wrapper.getWritten() + " segmentServer.getSize(): " + segmentServer.getSize());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            segmentsWritten += segmentServer.getSize();
        }
        if (this.data != null) {
            this.data.close();
        }
    }
}