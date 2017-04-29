package mnix.mobilecloud.communication.server;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

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
import mnix.mobilecloud.network.NetworkUtils;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static mnix.mobilecloud.communication.CommunicationUtils.createContentDisposition;
import static mnix.mobilecloud.communication.CommunicationUtils.createContentDispositionContentType;

public class ServerSegmentCommunication {
    private final Context context;

    ServerSegmentCommunication(Context context) {
        this.context = context;
    }

    public void uploadSegment() {
        Log.e("MOBILE CLOUD", "uploadSegment");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        InetAddress inetAddress = NetworkUtils.getGatewayAddress(wifiManager);
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, 8090);
        String boundary = "------WebKitFormBoundarysofJ3z07Xp5sALd2";
        String boundaryWithLine = boundary + "\r\n";
        String qquuid = UUID.randomUUID().toString();
        String qqfilename = "iron.txt";
        String data = "21312456";
        Integer qqtotalfilesize = data.length();
        String contentDispositionQquuid = createContentDisposition(boundaryWithLine, "qquuid", qquuid);
        String contentDispositionQqfilename = createContentDisposition(boundaryWithLine, "qqfilename", qqfilename);
        String contentDispositionQqtotalfilesize = createContentDisposition(boundaryWithLine, "qqtotalfilesize", "" + qqtotalfilesize);
        String contentDispositionQqfile = createContentDispositionContentType(boundaryWithLine, "qqfile", qqfilename, "text/plain");
        String footer = "\r\n" + boundary + "--\r\n";
        String payload = contentDispositionQquuid + contentDispositionQqfilename + contentDispositionQqtotalfilesize + contentDispositionQqfile + data + footer;
        ByteBuf bbuf = Unpooled.copiedBuffer(payload, Charset.defaultCharset());
        HttpClient.newClient(socketAddress)
//                .enableWireLogging("hello-client", LogLevel.ERROR)
                .createPost("/segment/upload")
//                .addHeader(HttpHeaderNames.CONTENT_LENGTH, payload.length())
//                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes())
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
//                .addHeader(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON)
//                .addHeader(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP_DEFLATE)
                .addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.MULTIPART_FORM_DATA + "; " + HttpHeaderValues.BOUNDARY + "=" + boundary.substring(2))
                .writeContent(Observable.just(bbuf))
                .flatMap(new Func1<HttpClientResponse<ByteBuf>, Observable<?>>() {
                    @Override
                    public Observable<?> call(HttpClientResponse<ByteBuf> resp) {
                        return resp.getContent()
                                .map(new Func1<ByteBuf, Object>() {
                                    @Override
                                    public Object call(ByteBuf bb) {
                                        return bb.toString(Charset.defaultCharset());
                                    }
                                });
                    }
                })
//                .toBlocking()
                .forEach(new Action1<Object>() {
                    @Override
                    public void call(Object i) {
                        Log.e("MOBILE CLOUD", (String) i);
                    }
                });
    }

}
