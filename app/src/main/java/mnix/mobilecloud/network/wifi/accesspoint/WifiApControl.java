package mnix.mobilecloud.network.wifi.accesspoint;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mnix.mobilecloud.network.NetworkManager;
import mnix.mobilecloud.network.wifi.WifiControl;

final public class WifiApControl {

    private static final String TAG = "WifiApControl";

    private static Method getWifiApConfigurationMethod;
    private static Method getWifiApStateMethod;
    private static Method isWifiApEnabledMethod;
    private static Method setWifiApEnabledMethod;

    static {
        for (Method method : WifiManager.class.getDeclaredMethods()) {
            switch (method.getName()) {
                case "getWifiApConfiguration":
                    getWifiApConfigurationMethod = method;
                    break;
                case "getWifiApState":
                    getWifiApStateMethod = method;
                    break;
                case "isWifiApEnabled":
                    isWifiApEnabledMethod = method;
                    break;
                case "setWifiApEnabled":
                    setWifiApEnabledMethod = method;
                    break;
            }
        }
    }

    private static boolean isSoftwareSupported() {
        return (getWifiApStateMethod != null
                && isWifiApEnabledMethod != null
                && setWifiApEnabledMethod != null
                && getWifiApConfigurationMethod != null);
    }

    public static boolean isSupported() {
        return isSoftwareSupported();
    }

    private final WifiManager wifiManager;

    private static WifiApControl instance = null;

    private WifiApControl(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiApControl getInstance(Context context) {
        if (instance == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context)) {
                Log.e(TAG, "6.0 or later, but haven't been granted WRITE_SETTINGS!");
                return null;
            }
            instance = new WifiApControl(context);
        }
        return instance;
    }

    private static Object invokeQuietly(Method method, Object receiver, Object... args) {
        try {
            return method.invoke(receiver, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    private boolean isWifiApEnabled() {
        Object result = invokeQuietly(isWifiApEnabledMethod, wifiManager);
        if (result == null) {
            return false;
        }
        return (Boolean) result;
    }

    public boolean isEnabled() {
        return isWifiApEnabled();
    }

    private int getWifiApState() {
        Object result = invokeQuietly(getWifiApStateMethod, wifiManager);
        if (result == null) {
            return -1;
        }
        return (Integer) result;
    }

    public int getState() {
        return getWifiApState();
    }

    private WifiConfiguration getWifiApConfiguration() {
        Object result = invokeQuietly(getWifiApConfigurationMethod, wifiManager);
        if (result == null) {
            return null;
        }
        return (WifiConfiguration) result;
    }

    private boolean setWifiApEnabled(WifiConfiguration config, boolean enabled) {
        Object result = invokeQuietly(setWifiApEnabledMethod, wifiManager, config, enabled);
        if (result == null) {
            return false;
        }
        return (Boolean) result;
    }

    public boolean setEnabled(WifiConfiguration config, boolean enabled) {
        return setWifiApEnabled(config, enabled);
    }

    public boolean enable() {
        return setEnabled(WifiControl.getConfiguration(), true);
    }

    public boolean disable() {
        return setEnabled(null, false);
    }
}