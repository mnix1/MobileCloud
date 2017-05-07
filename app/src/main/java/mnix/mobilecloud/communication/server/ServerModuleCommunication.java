package mnix.mobilecloud.communication.server;


import android.content.Context;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.module.ModuleError;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.client.ClientWebServer;
import rx.Observable;
import rx.functions.Func1;

public class ServerModuleCommunication {
    private final Context context;

    public ServerModuleCommunication(Context context) {
        this.context = context;
    }

    public Observable<Integer> count(final String address, String params) {
        Util.log(this.getClass(), "count", "machineServer: " + address + ", params: " + params);
        SocketAddress socketAddress = new InetSocketAddress(address, ClientWebServer.PORT);
        ByteBuf bbuf = Unpooled.copiedBuffer(params, Charset.defaultCharset());
        return HttpClient.newClient(socketAddress)
                .createPost("/module/count")
                .addHeader(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes())
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED)
                .writeContentAndFlushOnEach(Observable.just(bbuf))
                .flatMap(new Func1<HttpClientResponse<ByteBuf>, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(HttpClientResponse<ByteBuf> resp) {
                        return resp.getStatus().code() == 200
                                ? resp.getContent()
                                .map(new Func1<ByteBuf, Integer>() {
                                    @Override
                                    public Integer call(ByteBuf bb) {
                                        String res = bb.toString(Charset.defaultCharset());
                                        Util.log(this.getClass(), "count", "machineServer: " + address + ", response: " + res);
                                        return Integer.parseInt(res);
                                    }
                                })
                                : Observable.just(-1);
                    }
                });
    }
}
