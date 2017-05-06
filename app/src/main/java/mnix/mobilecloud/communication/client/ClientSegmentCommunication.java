package mnix.mobilecloud.communication.client;


import android.content.Context;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import mnix.mobilecloud.domain.client.MachineClient;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.network.NetworkUtils;
import mnix.mobilecloud.repository.client.MachineClientRepository;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.client.ClientWebServer;
import mnix.mobilecloud.web.server.ServerWebServer;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static mnix.mobilecloud.communication.CommunicationUtils.createContentDisposition;
import static mnix.mobilecloud.communication.CommunicationUtils.createContentDispositionContentType;

public class ClientSegmentCommunication {
    private final Context context;

    public ClientSegmentCommunication(Context context) {
        this.context = context;
    }

    public Boolean uploadSegment(SegmentClient segmentClient, String newSegmentIdentifier, String ipAddress) {
        Util.log(this.getClass(), "uploadSegment", "newSegmentIdentifier: " + newSegmentIdentifier + ", ipAddress: " + ipAddress);
        SocketAddress socketAddress = new InetSocketAddress(ipAddress, ClientWebServer.PORT);
        String boundary = "------" + newSegmentIdentifier;
        String boundaryWithLine = boundary + "\r\n";
        String qquuid = newSegmentIdentifier;
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
                .createPost("/segment/upload?notifyServer=1")
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


    public void updateSegment(final SegmentClient segmentClient) {
        Util.log(this.getClass(), "updateSegment");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        InetAddress inetAddress = NetworkUtils.getGatewayAddress(wifiManager);
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, ServerWebServer.PORT);
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
                    }
                });


    }
}
