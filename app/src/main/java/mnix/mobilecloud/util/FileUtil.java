package mnix.mobilecloud.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import mnix.mobilecloud.domain.client.SegmentClient;

public class FileUtil {
    public static File rootDir;

    public static String getRootDir() {
        return Environment.getExternalStorageDirectory() + "/MobileCloud";
    }

    public static void setDir() {
        rootDir = new File(getRootDir());
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }

    public static byte[] getData(SegmentClient segmentClient) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(getRootDir() + "/" + segmentClient.getIdentifier());
            byte[] data = new byte[segmentClient.getSize().intValue()];
            inputStream.read(data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setData(SegmentClient segmentClient, byte[] data) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(getRootDir() + "/" + segmentClient.getIdentifier());
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                    outputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void delete(SegmentClient segmentClient) {
        File file = new File(getRootDir() + "/" + segmentClient.getIdentifier());
        file.delete();
    }

    public static void clear() {
        for (File file : rootDir.listFiles()) {
            file.delete();
        }
    }
}
