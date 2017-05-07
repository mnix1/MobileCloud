package mnix.mobilecloud.network.wifi.observable;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

import mnix.mobilecloud.network.wifi.WifiNetwork;
import mnix.mobilecloud.network.wifi.WifiState;
import mnix.mobilecloud.network.wifi.accesspoint.AccessPointNotAvailable;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class ObservableWifi {
    public static Observable<ScanResult> observeWifiAp(final Context context, final String ssid) {
        return observeWifiApScan(context).map(new Func1<List<ScanResult>, ScanResult>() {
            @Override
            public ScanResult call(List<ScanResult> scanResults) {
                ScanResult scanResult = Observable.from(scanResults).filter(new Func1<ScanResult, Boolean>() {
                    @Override
                    public Boolean call(ScanResult scanResult) {
                        return scanResult.SSID.equals(ssid);
                    }
                }).toBlocking().first();
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
                } else if (scanResults.size() > 0) {
                    subject.onNext(scanResults);
                    subject.onCompleted();
                } else {
                }
            }
        };
        return subject.doOnCompleted(new Unregister(context, receiver)).doOnSubscribe(new Register(context, receiver, filter)).doOnUnsubscribe(new Unregister(context, receiver));
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
        return subject.doOnSubscribe(new Register(context, receiver, filter)).doOnUnsubscribe(new Unregister(context, receiver));
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
        return subject.doOnSubscribe(new Register(context, receiver, filter)).doOnUnsubscribe(new Unregister(context, receiver));
    }

    public static class Register implements Action0 {
        Context context;
        BroadcastReceiver receiver;
        IntentFilter filter;

        Register(Context context, BroadcastReceiver receiver, IntentFilter filter) {
            this.context = context;
            this.receiver = receiver;
            this.filter = filter;
        }

        @Override
        public void call() {
            context.registerReceiver(receiver, filter);
        }
    }

    public static class Unregister implements Action0 {
        Context context;
        BroadcastReceiver receiver;

        Unregister(Context context, BroadcastReceiver receiver) {
            this.context = context;
            this.receiver = receiver;
        }

        @Override
        public void call() {
            context.unregisterReceiver(receiver);
        }
    }
}
