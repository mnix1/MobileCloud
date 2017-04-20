package mnix.mobilecloud.file;

import android.os.Environment;

import java.io.File;

public class FileUtils {
    public static File STORAGE_DIR;
    public static File ROOT_DIR;

    public static void setDirs() {
        String rootDirPath = Environment.getExternalStorageDirectory() + "/SmartCloud";
        ROOT_DIR = new File(rootDirPath);
        STORAGE_DIR = new File(rootDirPath + "/storage");
        if (!ROOT_DIR.exists()) {
            ROOT_DIR.mkdirs();
        }
        if (!STORAGE_DIR.exists()) {
            STORAGE_DIR.mkdirs();
        }
    }
}
