package mnix.mobilecloud.network.wifi;

import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WifiNetwork {

    public final NetworkInfo networkInfo;
//    final WifiInfo wifiInfo;

    public WifiNetwork(Intent intent) {
        networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//        wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
    }

    @Override
    public String toString() {
        return "WIFI NETWORK: state " + networkInfo.getState() + " ssid " + networkInfo.getExtraInfo();
    }
}
