package mnix.mobilecloud.communication.server;


import android.content.Context;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
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
                .addHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .toBlocking()
                .first();
    }
}
