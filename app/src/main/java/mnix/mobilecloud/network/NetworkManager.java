package mnix.mobilecloud.network;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.MainActivity;
import mnix.mobilecloud.network.wifi.WifiControl;
import mnix.mobilecloud.network.wifi.WifiNetwork;
import mnix.mobilecloud.network.wifi.WifiState;
import mnix.mobilecloud.network.wifi.accesspoint.WifiApControl;
import mnix.mobilecloud.network.wifi.observable.ObservableWifi;
import mnix.mobilecloud.util.Util;

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
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
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

    public Maybe<MachineRole> connectOrCreateAp() {
        final PublishSubject<MachineRole> subject = PublishSubject.create();
        final Disposable wifiStateChange = ObservableWifi.observeWifiStateChange(context)
                .subscribe(new Consumer<WifiState>() {
                    @Override
                    public void accept(@NonNull WifiState wifiState) throws Exception {
                        if (wifiState != WifiState.WIFI_ENABLED && wifiState != WifiState.WIFI_ENABLING
                                && wifiState != WifiState.WIFI_AP_DISABLING && wifiState != WifiState.WIFI_AP_ENABLED && wifiState != WifiState.WIFI_AP_ENABLING) {
                            NetworkManager.this.enableWifi();
                        }
                    }
                });

        final Disposable wifiNetworkChange = ObservableWifi.observeWifiNetworkChange(context)
                .subscribe(new Consumer<WifiNetwork>() {
                    @Override
                    public void accept(@NonNull WifiNetwork wifiNetwork) throws Exception {
                        NetworkInfo networkInfo = wifiNetwork.networkInfo;
                        String ssid = networkInfo.getExtraInfo();
                        if (ssid.contains(SSID)) {
                            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                                subject.onNext(MachineRole.SLAVE);
                                subject.onComplete();
                            }
                        } else {
                            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED) || networkInfo.getState().equals(NetworkInfo.State.CONNECTING)) {
                                wifiControl.disconnectWifi();
                            }
                        }
                    }
                });

        final Disposable wifiApScan = ObservableWifi.observeWifiAp(context, SSID)
                .subscribe(new Consumer<ScanResult>() {
                    @Override
                    public void accept(@NonNull ScanResult scanResult) throws Exception {
                        Util.log("Scan Results: MobileCloud available");
                        wifiStateChange.dispose();
                        wifiNetworkChange.dispose();
                        wifiControl.connectWifi();
                        subject.onNext(MachineRole.SLAVE);
                        subject.onComplete();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable exc) throws Exception {
                        wifiStateChange.dispose();
                        wifiNetworkChange.dispose();
                        if (!WifiApControl.isSupported()) {
                            wifiControl.connectWifi();
                            return;
                        }
                        Util.log("Scan Results: MobileCloud not available");
                        NetworkManager.this.enableAp();
                        subject.onNext(MachineRole.MASTER);
                        subject.onComplete();
                    }
                });
        Action done = new Action() {
            @Override
            public void run() throws Exception {
                wifiStateChange.dispose();
                wifiNetworkChange.dispose();
                wifiApScan.dispose();
            }
        };
        return subject.doOnDispose(done).doOnComplete(done).firstElement();
    }
}
