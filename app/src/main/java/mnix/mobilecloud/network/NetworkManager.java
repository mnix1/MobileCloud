package mnix.mobilecloud.network;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;

import java.util.concurrent.TimeUnit;

import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.MainActivity;
import mnix.mobilecloud.network.wifi.WifiControl;
import mnix.mobilecloud.network.wifi.WifiNetwork;
import mnix.mobilecloud.network.wifi.WifiState;
import mnix.mobilecloud.network.wifi.accesspoint.WifiApControl;
import mnix.mobilecloud.network.wifi.observable.ObservableWifi;
import mnix.mobilecloud.util.Util;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class NetworkManager {
    public static final String SSID = "MobileCloud";
    public static final String PASSWORD = "mobilecloud1";
    private WifiControl wifiControl;
    private Context context;

    public NetworkManager(final MainActivity activity) {
        context = activity.getApplicationContext();
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        activity.updateWifiInfo(wifiControl.log());
                    }
                });
        wifiControl = WifiControl.getInstance(context);
    }

    public void enableAp() {
        wifiControl.enableAp();
    }

    public WifiControl getWifiControl() {
        return wifiControl;
    }

    public void enableWifi() {
        wifiControl.enableWifi();
    }

    public Observable<MachineRole> connectOrCreateAp() {
        final PublishSubject<MachineRole> subject = PublishSubject.create();
        final Subscription wifiStateChange = ObservableWifi.observeWifiStateChange(context)
                .subscribe(new Action1<WifiState>() {
                    @Override
                    public void call(WifiState wifiState) {
                        if (wifiState != WifiState.WIFI_ENABLED && wifiState != WifiState.WIFI_ENABLING
                                && wifiState != WifiState.WIFI_AP_DISABLING && wifiState != WifiState.WIFI_AP_ENABLED && wifiState != WifiState.WIFI_AP_ENABLING) {
                            NetworkManager.this.enableWifi();
                        }
                    }
                });

        final Subscription wifiNetworkChange = ObservableWifi.observeWifiNetworkChange(context)
                .subscribe(new Action1<WifiNetwork>() {
                    @Override
                    public void call(WifiNetwork wifiNetwork) {
                        NetworkInfo networkInfo = wifiNetwork.networkInfo;
                        String ssid = networkInfo.getExtraInfo();
                        if (ssid.contains(SSID)) {
                            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                                subject.onNext(MachineRole.SLAVE);
                                subject.onCompleted();
                            }
                        } else {
                            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED) || networkInfo.getState().equals(NetworkInfo.State.CONNECTING)) {
                                wifiControl.disconnectWifi();
                            }
                        }
                    }
                });

        final Subscription wifiApScan = ObservableWifi.observeWifiAp(context, SSID)
                .subscribe(new Action1<ScanResult>() {
                    @Override
                    public void call(ScanResult scanResult) {
                        Util.log("Scan Results: MobileCloud available");
                        wifiStateChange.unsubscribe();
                        wifiNetworkChange.unsubscribe();
                        wifiControl.connectWifi();
                        subject.onNext(MachineRole.SLAVE);
                        subject.onCompleted();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        wifiStateChange.unsubscribe();
                        wifiNetworkChange.unsubscribe();
                        if (!WifiApControl.isSupported()) {
                            wifiControl.connectWifi();
                            return;
                        }
                        Util.log("Scan Results: MobileCloud not available");
                        NetworkManager.this.enableAp();
                        subject.onNext(MachineRole.MASTER);
                        subject.onCompleted();
                    }
                });
        Action0 done = new Action0() {
            @Override
            public void call() {
                wifiStateChange.unsubscribe();
                wifiNetworkChange.unsubscribe();
                wifiApScan.unsubscribe();
            }
        };
        return subject.doOnUnsubscribe(done).doOnCompleted(done).first();
    }
}
