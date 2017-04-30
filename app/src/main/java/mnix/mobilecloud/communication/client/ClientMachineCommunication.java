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
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import mnix.mobilecloud.domain.client.MachineClient;
import mnix.mobilecloud.network.NetworkUtils;
import mnix.mobilecloud.repository.client.MachineClientRepository;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.server.ServerWebServer;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ClientMachineCommunication {
    private final Context context;

    public ClientMachineCommunication(Context context) {
        this.context = context;
    }

    public void updateMachine() {
        Util.log(this.getClass(), "updateMachine");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        InetAddress inetAddress = NetworkUtils.getGatewayAddress(wifiManager);
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, ServerWebServer.PORT);
        MachineClient machineClient = MachineClientRepository.get();
        String params = machineClient.toParams();
        ByteBuf bbuf = Unpooled.copiedBuffer(params, Charset.defaultCharset());
        HttpClient.newClient(socketAddress)
                .enableWireLogging("hello-client", LogLevel.ERROR)
                .createPost("/machine/update")
                .addHeader(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes())
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED)
                .writeContentAndFlushOnEach(Observable.just(bbuf))
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
                        Util.log(this.getClass(), "updateMachine", "response");
                    }
                });
    }
}
