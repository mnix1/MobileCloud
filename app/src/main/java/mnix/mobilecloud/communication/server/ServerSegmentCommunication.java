package mnix.mobilecloud.communication.server;


import android.content.Context;

import org.nanohttpd.protocols.http.response.StreamingResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import mnix.mobilecloud.communication.CommunicationUtils;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.client.ClientWebServer;
import rx.functions.Action1;
import rx.functions.Func1;

public class ServerSegmentCommunication {
    private final Context context;

    public ServerSegmentCommunication(Context context) {
        this.context = context;
    }

    public Boolean uploadSegment(SegmentClient segmentClient, String address) {
        Util.log(this.getClass(), "uploadSegment", "segmentClient: " + segmentClient + "address: " + address);
        return CommunicationUtils.uploadSegment(segmentClient, address, "/segment/upload");
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
