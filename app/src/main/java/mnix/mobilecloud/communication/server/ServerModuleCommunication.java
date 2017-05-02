package mnix.mobilecloud.communication.server;


import android.content.Context;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
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

//    public int count(MachineServer machineServer, String params) throws ModuleError {
//        Util.log(this.getClass(), "count", "machineServer: " + machineServer + ", params: " + params);
//        SocketAddress socketAddress = new InetSocketAddress(machineServer.getIpAddress(), ClientWebServer.PORT);
//        Integer result = HttpClient.newClient(socketAddress)
//                .createGet("/module/count" + params)
//                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
////                .map(new Func1<HttpClientResponse<ByteBuf>, Integer>() {
////                    @Override
////                    public Integer call(HttpClientResponse<ByteBuf> response) {
////                        return response.getStatus().code() == 200 ? Integer.getInteger(response.getContent().map())) : -1;
////                    }
////                })
//                .flatMap(new Func1<HttpClientResponse<ByteBuf>, Observable<Integer>>() {
//                    @Override
//                    public Observable<Integer> call(HttpClientResponse<ByteBuf> resp) {
//                        return resp.getStatus().code() == 200
//                                ? resp.getContent()
//                                .map(new Func1<ByteBuf, Integer>() {
//                                    @Override
//                                    public Integer call(ByteBuf bb) {
//                                        String res = bb.toString(Charset.defaultCharset());
//                                        Util.log(this.getClass(), "count", "response: " + res);
//                                        return Integer.getInteger(res);
//                                    }
//                                })
//                                : Observable.just(-1);
//                    }
//                })
//                .toBlocking()
//                .first();
//        if (result == -1) {
//            throw new ModuleError();
//        }
//        return result;
//    }


    public Observable<Integer> count(MachineServer machineServer, String params) {
        Util.log(this.getClass(), "count", "machineServer: " + machineServer + ", params: " + params);
        SocketAddress socketAddress = new InetSocketAddress(machineServer.getIpAddress(), ClientWebServer.PORT);
        return HttpClient.newClient(socketAddress)
                .createGet("/module/count" + params)
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .flatMap(new Func1<HttpClientResponse<ByteBuf>, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(HttpClientResponse<ByteBuf> resp) {
                        return resp.getStatus().code() == 200
                                ? resp.getContent()
                                .map(new Func1<ByteBuf, Integer>() {
                                    @Override
                                    public Integer call(ByteBuf bb) {
                                        String res = bb.toString(Charset.defaultCharset());
                                        Util.log(this.getClass(), "count", "response: " + res);
                                        return Integer.parseInt(res);
                                    }
                                })
                                : Observable.just(-1);
                    }
                });
    }
}
