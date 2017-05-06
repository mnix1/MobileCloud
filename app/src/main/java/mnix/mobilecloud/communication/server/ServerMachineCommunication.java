package mnix.mobilecloud.communication.server;


import android.content.Context;

import com.google.gson.Gson;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import mnix.mobilecloud.domain.client.MachineClient;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.client.ClientWebServer;
import rx.Observable;
import rx.functions.Func1;

public class ServerMachineCommunication {
    private final Context context;

    public ServerMachineCommunication(Context context) {
        this.context = context;
    }

    public MachineClient getMachine(String address) {
        Util.log(this.getClass(), "getMachine", "address: " + address);
        SocketAddress socketAddress = new InetSocketAddress(address, ClientWebServer.PORT);
        String response = HttpClient.newClient(socketAddress)
//                .enableWireLogging("hello-client", LogLevel.ERROR)
                .createGet("/machine/get")
                .flatMap(new Func1<HttpClientResponse<ByteBuf>, Observable<String>>() {
                    @Override
                    public Observable<String> call(HttpClientResponse<ByteBuf> resp) {
                        return resp.getContent()
                                .map(new Func1<ByteBuf, String>() {
                                    @Override
                                    public String call(ByteBuf bb) {
                                        return bb.toString(Charset.defaultCharset());
                                    }
                                });
                    }
                })
                .toBlocking()
                .first();
        return new Gson().fromJson(response, MachineClient.class);
    }
}
