package mnix.mobilecloud.util;

import android.util.Log;

public class Util {
    public static void log(String log) {
        Log.e("MoblieCloud", log);
    }

    public static void log(Class clazz, String method) {
        Log.e("MoblieCloud/" + clazz.getSimpleName(), method);
    }

    public static void log(Class clazz, String method, String log) {
        Log.e("MoblieCloud/" + clazz.getSimpleName(), method + ": " + log);
    }

    public static String cutUuid(String uuid) {
        return uuid.substring(0, 8);
    }
}
