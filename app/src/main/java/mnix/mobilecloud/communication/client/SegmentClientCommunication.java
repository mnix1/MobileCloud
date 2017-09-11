package mnix.mobilecloud.communication.client;


import android.content.Context;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import mnix.mobilecloud.communication.CommunicationUtils;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.network.NetworkUtil;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.server.WebServerServer;
import rx.Observable;
import rx.functions.Action1;

public class SegmentClientCommunication {
    private final Context context;

    public SegmentClientCommunication(Context context) {
        this.context = context;
    }

    public Boolean uploadSegment(SegmentClient segmentClient, String address) {
        Util.log(this.getClass(), "uploadSegment", "segmentClient: " + segmentClient + "address: " + address);
        return CommunicationUtils.uploadSegment(segmentClient, address, "/segment/uploadWithNotifyServer");
    }


    public void updateSegment(final SegmentClient segmentClient) {
        Util.log(this.getClass(), "updateSegment");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        InetAddress inetAddress = NetworkUtil.getGatewayAddress(wifiManager);
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, WebServerServer.PORT);
        String params = segmentClient.toParams();
        ByteBuf bbuf = Unpooled.copiedBuffer(params, Charset.defaultCharset());
        HttpClient.newClient(socketAddress)
//                .enableWireLogging("hello-client", LogLevel.ERROR)
                .createPost("/segment/update")
                .addHeader(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes())
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED)
                .writeContentAndFlushOnEach(Observable.just(bbuf))
                .forEach(new Action1<HttpClientResponse<ByteBuf>>() {
                    @Override
                    public void call(HttpClientResponse<ByteBuf> byteBufHttpClientResponse) {
                        if (byteBufHttpClientResponse.getStatus().code() == 200) {
                            Util.log(this.getClass(), "updateSegment", "segmentClient save");
                            segmentClient.save();
                        }
                        byteBufHttpClientResponse.discardContent().subscribe().unsubscribe();
                    }
                });


    }
}
