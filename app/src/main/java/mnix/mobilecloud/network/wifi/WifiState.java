package mnix.mobilecloud.network.wifi;

import android.net.wifi.WifiManager;

public enum WifiState {
    WIFI_DISABLING(WifiManager.WIFI_STATE_DISABLING, "Wifi disabling"),
    WIFI_DISABLED(WifiManager.WIFI_STATE_DISABLED, "Wifi disabled"),
    WIFI_ENABLING(WifiManager.WIFI_STATE_ENABLING, "Wifi enabling"),
    WIFI_ENABLED(WifiManager.WIFI_STATE_ENABLED, "Wifi enabled"),
    WIFI_UNKNOWN(WifiManager.WIFI_STATE_UNKNOWN, "Wifi unknown"),
    WIFI_AP_DISABLING(10, "Ap disabling"),
    WIFI_AP_DISABLED(11, "Ap disabled"),
    WIFI_AP_ENABLING(12, "Ap enabling"),
    WIFI_AP_ENABLED(13, "Ap enabled"),
    WIFI_AP_FAILED(14, "Ap failed"),
    ERROR(100, "error");

    public final int state;
    public final String description;

    WifiState(final int state, String description) {
        this.state = state;
        this.description = description;
    }

    public static WifiState fromState(int state) {
        WifiState[] values = WifiState.values();
        for (int i = 0; i < values.length; i++) {
            WifiState value = values[i];
            if (state == value.state) {
                return value;
            }
        }
        return WifiState.ERROR;
    }

    @Override
    public String toString() {
        return "WIFI STATE: " + description + " state: " + state;
    }
}
