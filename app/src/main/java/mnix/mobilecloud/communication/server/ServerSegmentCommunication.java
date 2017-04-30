package mnix.mobilecloud.communication.server;


import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.client.ClientWebServer;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static mnix.mobilecloud.communication.CommunicationUtils.createContentDisposition;
import static mnix.mobilecloud.communication.CommunicationUtils.createContentDispositionContentType;

public class ServerSegmentCommunication {
    private final Context context;

    public ServerSegmentCommunication(Context context) {
        this.context = context;
    }

    public Boolean uploadSegment(SegmentClient segmentClient, MachineServer machineServer) {
        Util.log(this.getClass(), "uploadSegment", "machineServer: " + machineServer);
        SocketAddress socketAddress = new InetSocketAddress(machineServer.getIpAddress(), ClientWebServer.PORT);
        String boundary = "------" + segmentClient.getIdentifier();
        String boundaryWithLine = boundary + "\r\n";
        String qquuid = segmentClient.getIdentifier();
        String qqfilename = segmentClient.getFileIdentifier();
        byte[] data = segmentClient.getData();
        Integer qqtotalfilesize = data.length;
        String contentDispositionQquuid = createContentDisposition(boundaryWithLine, "qquuid", qquuid);
        String contentDispositionQqfilename = createContentDisposition(boundaryWithLine, "qqfilename", qqfilename);
        String contentDispositionQqtotalfilesize = createContentDisposition(boundaryWithLine, "qqtotalfilesize", "" + qqtotalfilesize);
        String contentDispositionQqfile = createContentDispositionContentType(boundaryWithLine, "qqfile", qqfilename, "text/plain");
        String footer = "\r\n" + boundary + "--\r\n";
        String payload = contentDispositionQquuid + contentDispositionQqfilename + contentDispositionQqtotalfilesize + contentDispositionQqfile;
        ByteBuf bbuf = Unpooled.copiedBuffer(payload, Charset.defaultCharset());
        bbuf.writeBytes(data);
        bbuf.writeCharSequence(footer, Charset.defaultCharset());
        return HttpClient.newClient(socketAddress)
//                .enableWireLogging("hello-client", LogLevel.ERROR)
                .createPost("/segment/upload")
                .addHeader(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes())
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.MULTIPART_FORM_DATA + "; " + HttpHeaderValues.BOUNDARY + "=" + boundary.substring(2))
                .writeContentAndFlushOnEach(Observable.just(bbuf))
                .map(new Func1<HttpClientResponse<ByteBuf>, Boolean>() {
                    @Override
                    public Boolean call(HttpClientResponse<ByteBuf> response) {
                        return response.getStatus().code() == 200;
                    }
                })
                .toBlocking()
                .first();
    }


    public InputStream downloadSegment(SegmentServer segmentServer, MachineServer machineServer) {
        Util.log(this.getClass(), "downloadSegment", "segmentServer: " + segmentServer + ", machineServer: " + machineServer);
        SocketAddress socketAddress = new InetSocketAddress(machineServer.getIpAddress(), ClientWebServer.PORT);
        int size = (int) (segmentServer.getByteTo() - segmentServer.getByteFrom() + 1);
        final ByteBuf output = Unpooled.buffer(size);
        HttpClient.newClient(socketAddress)
                .createGet("/segment/download?identifier=" + segmentServer.getIdentifier())
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .toBlocking()
                .forEach(new Action1<HttpClientResponse<ByteBuf>>() {
                    @Override
                    public void call(HttpClientResponse<ByteBuf> response) {
                        response.getContent().forEach(new Action1<ByteBuf>() {
                            @Override
                            public void call(ByteBuf byteBuf) {
                                output.writeBytes(byteBuf);
                                Util.log(this.getClass(), "call", "readableBytes: " + output.readableBytes());
                            }
                        });
                    }
                });
        return new ByteArrayInputStream(output.array());


//                .flatMap(new Func1<HttpClientResponse<ByteBuf>, Observable<?>>() {
//                    @Override
//                    public Observable<?> call(HttpClientResponse<ByteBuf> resp) {
//                        return resp.getContent()
//                                .map(new Func1<ByteBuf, Object>() {
//                                    @Override
//                                    public Object call(ByteBuf bb) {
//                                        return bb.toString(Charset.defaultCharset());
//                                    }
//                                });
//                    }
//                })
//                .toBlocking()
//                .forEach(new Action1<Object>() {
//                    @Override
//                    public void call(Object i) {
//                        Util.log(this.getClass(), "downloadSegment", "response: " + i);
//                    }
//                });
//         .map(new Func1<HttpClientResponse<ByteBuf>, InputStream>() {
//            @Override
//            public InputStream call(HttpClientResponse<ByteBuf> response) {
//                ByteBuf byteBuf = response.getContent().toBlocking().first();
//                return new ByteArrayInputStream(byteBuf.array());
//            }
//        })
//                .toBlocking()
//                .first();
    }

    public Boolean deleteSegment(SegmentServer segmentServer, MachineServer machineServer) {
        Util.log(this.getClass(), "downloadSegment", "segmentServer: " + segmentServer + ", machineServer: " + machineServer);
        SocketAddress socketAddress = new InetSocketAddress(machineServer.getIpAddress(), ClientWebServer.PORT);
        return HttpClient.newClient(socketAddress)
                .createGet("/segment/delete?identifier=" + segmentServer.getIdentifier())
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .map(new Func1<HttpClientResponse<ByteBuf>, Boolean>() {
                    @Override
                    public Boolean call(HttpClientResponse<ByteBuf> response) {
                        return response.getStatus().code() == 200;
                    }
                })
                .toBlocking()
                .first();
    }

}
