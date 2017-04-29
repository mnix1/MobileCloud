package mnix.mobilecloud.communication;


public class CommunicationUtils {

    public static String createContentDisposition(String boundary, String name, String data) {
        return boundary + "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n" + data + "\r\n";
    }

    public static String createContentDispositionContentType(String boundary, String name, String filename, String contentType) {
        return boundary + "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\nContent-Type: " + contentType + "\r\n\r\n";
    }

}
