package mnix.mobilecloud.network.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.util.List;

import mnix.mobilecloud.network.NetworkManager;
import mnix.mobilecloud.network.NetworkUtil;
import mnix.mobilecloud.network.wifi.accesspoint.WifiApControl;

public class WifiControl {
    private final WifiManager wifiManager;
    private final WifiApControl apControl;

    private static WifiControl instance = null;

    public static WifiConfiguration getConfiguration() {
        WifiConfiguration netConfig = new WifiConfiguration();
        netConfig.SSID = NetworkManager.SSID;
        netConfig.preSharedKey = NetworkManager.PASSWORD;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        netConfig.allowedKeyManagement.set(4);
        return netConfig;
    }

    private WifiControl(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        apControl = WifiApControl.getInstance(context);
    }

    public static WifiControl getInstance(Context context) {
        if (instance == null) {
            instance = new WifiControl(context);
        }
        return instance;
    }

    public void enableWifi() {
        apControl.disable();
        wifiManager.setWifiEnabled(true);
    }

    public void enableAp() {
        wifiManager.setWifiEnabled(false);
        apControl.enable();
    }

    public boolean isApEnabled() {
        return apControl.isEnabled();
    }

    public void connectWifi() {
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            wifiManager.disableNetwork(i.networkId);
            wifiManager.removeNetwork(i.networkId);
            wifiManager.saveConfiguration();
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + NetworkManager.SSID + "\"";
        config.status = WifiConfiguration.Status.DISABLED;
        config.priority = 40;
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.preSharedKey = "\"" + NetworkManager.PASSWORD + "\"";
        int networkId = wifiManager.addNetwork(config);
        if (networkId == -1) {
            return;
        }
        wifiManager.enableNetwork(networkId, true);
        wifiManager.saveConfiguration();
        wifiManager.reconnect();
    }

    public WifiApControl getApControl() {
        return apControl;
    }

    public void disconnectWifi() {
        wifiManager.disconnect();
    }

    public String log() {
        StringBuilder sb = new StringBuilder();
        if (!WifiApControl.isSupported()) {
            sb.append("Warning: Wifi AP mode not supported!\n");
            sb.append("You should get unknown or zero values below.\n");
            sb.append("If you don't, isSupported() is probably buggy!\n");
        }
        if (apControl == null) {
            sb.append("Something went wrong while trying to get AP control!\n");
            sb.append("Make sure to grant the app the WRITE_SETTINGS permission.");
            return sb.toString();
        }
        sb.append("Inet4Address: ");
        Inet4Address addr4 = NetworkUtil.getInet4Address();
        sb.append(addr4 == null ? "null" : addr4.toString()).append('\n');

        sb.append(WifiState.fromState(wifiManager.getWifiState())).append('\n');
        sb.append(WifiState.fromState(apControl.getState())).append('\n');

        if (apControl.isEnabled()) {
            WifiConfiguration config = getConfiguration();
            sb.append("ApConfiguration:");
            if (config == null) {
                sb.append(" null\n");
            } else {
                sb.append("\n");
                sb.append("   SSID: \"").append(config.SSID).append("\"\n");
                sb.append("   preSharedKey: \"").append(config.preSharedKey).append("\"\n");
            }
        }
        return sb.toString();
    }
}
