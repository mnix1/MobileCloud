package mnix.mobilecloud.communication.server;


import android.content.Context;

import org.nanohttpd.protocols.http.response.StreamingResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
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

    public Boolean uploadSegment(SegmentClient segmentClient, String address) {
        Util.log(this.getClass(), "uploadSegment", "address: " + address);
        SocketAddress socketAddress = new InetSocketAddress(address, ClientWebServer.PORT);
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


    public void downloadSegment(final SegmentServer segmentServer, String address, final StreamingResponse.StreamingResponseWrapper wrapper) {
        Util.log(this.getClass(), "downloadSegment", "segmentServer: " + segmentServer + ", address: " + address);
        SocketAddress socketAddress = new InetSocketAddress(address, ClientWebServer.PORT);
        HttpClient.newClient(socketAddress)
                .createGet("/segment/download?identifier=" + segmentServer.getIdentifier())
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .toBlocking()
                .forEach(new Action1<HttpClientResponse<ByteBuf>>() {
                    @Override
                    public void call(HttpClientResponse<ByteBuf> httpResponse) {
                        httpResponse.getContent().forEach(new Action1<ByteBuf>() {
                            @Override
                            public void call(ByteBuf byteBuf) {
                                try {
//                                    Util.log(this.getClass(), "downloadSegment", "byteBuf.readableBytes(): " + byteBuf.readableBytes());
                                    wrapper.sent(byteBuf.readableBytes());
                                    byteBuf.readBytes(wrapper.getOutputStream(), byteBuf.readableBytes());
                                } catch (IOException e) {
                                    Util.log(this.getClass(), "call", "ERROR");
                                }
                            }
                        });
                    }
                });
    }

    public Boolean deleteSegment(SegmentServer segmentServer, String address) {
        Util.log(this.getClass(), "deleteSegment", "segmentServer: " + segmentServer + ", address: " + address);
        SocketAddress socketAddress = new InetSocketAddress(address, ClientWebServer.PORT);
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
