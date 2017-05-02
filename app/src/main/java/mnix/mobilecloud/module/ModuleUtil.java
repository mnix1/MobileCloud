package mnix.mobilecloud.module;


import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ModuleUtil {
    public static byte[] getDataArg(Map<String, String> params) {
        byte[] data = new byte[0];
        if (params.containsKey("byte")) {
            String bytesString = params.get("byte");
            String[] splitBytesString = bytesString.split(",");
            data = new byte[splitBytesString.length];
            for (int i = 0; i < splitBytesString.length; i++) {
                data[i] = Byte.valueOf(splitBytesString[i]);
            }
        } else if (params.containsKey("string")) {
            String string = params.get("string");
            String encoding = params.containsKey("encoding") ? params.get("encoding") : "UTF-8";
            try {
                data = string.getBytes(encoding);
            } catch (UnsupportedEncodingException e) {
                data = new byte[0];
            }
        }
        return data;
    }
}
