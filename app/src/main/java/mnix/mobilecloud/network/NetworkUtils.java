package mnix.mobilecloud.network;

import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final List<String> deviceNames = Arrays.asList("wlan0", "swlan0");

    public static Inet4Address getInet4Address() {
        return getInetAddress(Inet4Address.class);
    }

    private static <T extends InetAddress> T getInetAddress(Class<T> addressType) {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();

                if (!deviceNames.contains(iface.getName())) {
                    continue;
                }

                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();

                    if (addressType.isInstance(addr)) {
                        return addressType.cast(addr);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    public InetAddress getGatewayAddress(WifiManager wifiManager) {
        List<String> arpTableRows = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equals("IP address       HW type     Flags       HW address            Mask     Device")) {
                    arpTableRows.add(line.substring(0, line.indexOf(" ")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!arpTableRows.isEmpty()) {
            try {
                return InetAddress.getByName(arpTableRows.get(0));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return inetAddressFromInt(wifiManager.getDhcpInfo().gateway);
    }

    public InetAddress inetAddressFromInt(int address) {
        try {
            return Inet4Address.getByAddress(new byte[]{
                    (byte) address, (byte) (address >>> 8), (byte) (address >>> 16), (byte) (address >>> 24)});
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
