package mnix.mobilecloud.communication.server;


import android.content.Context;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.reactivex.netty.protocol.http.client.HttpClient;
import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.client.WebServerClient;

public class FileServerCommunication {
    private final Context context;

    public FileServerCommunication(Context context) {
        this.context = context;
    }

    public void deleteFileSegments(FileServer fileServer, String address) {
        Util.log(this.getClass(), "deleteSegment", "fileServer: " + fileServer + ", address: " + address);
        SocketAddress socketAddress = new InetSocketAddress(address, WebServerClient.PORT);
        HttpClient.newClient(socketAddress)
                .createGet("/file/delete?identifier=" + fileServer.getIdentifier())
                .take(1);
    }

//    public Boolean deleteFileSegments(FileServer fileServer, MachineServer machineServer) {
//        Util.log(this.getClass(), "deleteSegment", "fileServer: " + fileServer + ", machineServer: " + machineServer);
//        SocketAddress socketAddress = new InetSocketAddress(machineServer.getIpAddress(), WebServerClient.PORT);
//        return HttpClient.newClient(socketAddress)
//                .createGet("/file/delete?identifier=" + fileServer.getIdentifier())
//                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
//                .map(new Func1<HttpClientResponse<ByteBuf>, Boolean>() {
//                    @Override
//                    public Boolean call(HttpClientResponse<ByteBuf> response) {
//                        return response.getStatus().code() == 200;
//                    }
//                })
//                .toBlocking()
//                .first();
//    }

}
