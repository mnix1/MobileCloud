package mnix.mobilecloud.network.wifi.observable;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import mnix.mobilecloud.network.wifi.WifiNetwork;
import mnix.mobilecloud.network.wifi.WifiState;
import mnix.mobilecloud.network.wifi.accesspoint.AccessPointNotAvailable;

public class ObservableWifi {
    public static Observable<ScanResult> observeWifiAp(final Context context, final String ssid) {
        return observeWifiApScan(context).map(new Function<List<ScanResult>, ScanResult>() {
            @Override
            public ScanResult apply(@NonNull List<ScanResult> scanResults) throws Exception {
                ScanResult scanResult = Observable.fromIterable(scanResults).filter(new Predicate<ScanResult>() {
                    @Override
                    public boolean test(@NonNull ScanResult scanResult) throws Exception {
                        return scanResult.SSID.equals(ssid);
                    }
                }).firstElement().blockingGet();
                if (scanResult == null) {
                    throw Exceptions.propagate(new AccessPointNotAvailable());
                }
                return scanResult;
            }
        });
    }


    public static Observable<List<ScanResult>> observeWifiApScan(final Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        final PublishSubject<List<ScanResult>> subject = PublishSubject.create();
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiManager.startScan();
                List<ScanResult> scanResults = wifiManager.getScanResults();
                if (scanResults == null) {
                    subject.onError(new EmptyResultScan());
                } else {
                    subject.onNext(scanResults);
                    subject.onComplete();
                }
            }
        };
        return subject.doOnComplete(new Unregister(context, receiver)).doOnSubscribe(new Register(context, receiver, filter)).doOnDispose(new Unregister(context, receiver));
    }

    public static Observable<WifiState> observeWifiStateChange(final Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        final PublishSubject<WifiState> subject = PublishSubject.create();
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 100);
                subject.onNext(WifiState.fromState(wifiState));
            }
        };
        Log.e("MOBILE CLOUD", "observeWifiStateChange registerReceiver");
        return subject.doOnSubscribe(new Register(context, receiver, filter)).doOnDispose(new Unregister(context, receiver));
    }

    public static Observable<WifiNetwork> observeWifiNetworkChange(final Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        final PublishSubject<WifiNetwork> subject = PublishSubject.create();
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                subject.onNext(new WifiNetwork(intent));
            }
        };
        return subject.doOnSubscribe(new Register(context, receiver, filter)).doOnDispose(new Unregister(context, receiver));
    }

    public static class Register implements Consumer {
        Context context;
        BroadcastReceiver receiver;
        IntentFilter filter;

        Register(Context context, BroadcastReceiver receiver, IntentFilter filter) {
            this.context = context;
            this.receiver = receiver;
            this.filter = filter;
        }

        @Override
        public void accept(@NonNull Object o) throws Exception {
            Log.e("MOBILE CLOUD", "Register");
            context.registerReceiver(receiver, filter);
        }
    }

    public static class Unregister implements Action {
        Context context;
        BroadcastReceiver receiver;

        Unregister(Context context, BroadcastReceiver receiver) {
            this.context = context;
            this.receiver = receiver;
        }

        @Override
        public void run() throws Exception {
            Log.e("MOBILE CLOUD", "Unregister");
            context.unregisterReceiver(receiver);
        }
    }
}
