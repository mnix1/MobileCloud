package mnix.mobilecloud.util;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.Date;

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

    public static long calculateSpeed() {
        Date startDate = new Date();
        byte[] data = new byte[1024 * 1024];
        byte[] countData = new byte[4];
        BinarySearcher binarySearcher = new BinarySearcher();
        binarySearcher.searchBytes(data, countData).size();
        Date endDate = new Date();
        long time = endDate.getTime() - startDate.getTime();
        return time;
    }

    public static long getSpace() {
        return Environment.getExternalStorageDirectory().getFreeSpace();
    }

    public static String cutUuid(String uuid) {
        return uuid.substring(0, 8);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public static String getSystem() {
        return "Android " + Build.VERSION.RELEASE;
    }

    public static String getId() {
        return Build.ID;
    }

    public static String getName() {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        return myDevice.getName();
    }

    public static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }
}
