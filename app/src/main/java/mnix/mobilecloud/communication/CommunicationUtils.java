package mnix.mobilecloud.communication;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.client.ClientWebServer;
import rx.Observable;
import rx.functions.Func1;

public class CommunicationUtils {

    public static String createContentDisposition(String boundary, String name, String data) {
        return boundary + "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n" + data + "\r\n";
    }

    public static String createContentDispositionContentType(String boundary, String name, String filename, String contentType) {
        return boundary + "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\nContent-Type: " + contentType + "\r\n\r\n";
    }

    public static Boolean uploadSegment(SegmentClient segmentClient, String ipAddress, String url) {
        SocketAddress socketAddress = new InetSocketAddress(ipAddress, ClientWebServer.PORT);
        String boundary = "------" + segmentClient.getIdentifier();
        String boundaryWithLine = boundary + "\r\n";
        String qquuid = segmentClient.getIdentifier();
        String qqfilename = segmentClient.getFileIdentifier();
        byte[] data = segmentClient.getData();
        Integer qqchunksize = data.length;
        String contentDispositionQquuid = createContentDisposition(boundaryWithLine, "qquuid", qquuid);
        String contentDispositionQqfilename = createContentDisposition(boundaryWithLine, "qqfilename", qqfilename);
        String contentDispositionQqpartbyteoffset = createContentDisposition(boundaryWithLine, "qqpartbyteoffset", "" + segmentClient.getByteFrom());
        String contentDispositionQqchunksize = createContentDisposition(boundaryWithLine, "qqchunksize", "" + qqchunksize);
        String contentDispositionQqfile = createContentDispositionContentType(boundaryWithLine, "qqfile", qqfilename, "text/plain");
        String footer = "\r\n" + boundary + "--\r\n";
        String payload = contentDispositionQquuid + contentDispositionQqfilename + contentDispositionQqpartbyteoffset +  contentDispositionQqchunksize + contentDispositionQqfile;
        ByteBuf bbuf = Unpooled.copiedBuffer(payload, Charset.defaultCharset());
        bbuf.writeBytes(data);
        bbuf.writeCharSequence(footer, Charset.defaultCharset());
        return HttpClient.newClient(socketAddress)
//                .enableWireLogging("hello-client", LogLevel.ERROR)
                .createPost(url)
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

}
