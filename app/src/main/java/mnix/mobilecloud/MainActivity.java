package mnix.mobilecloud;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.orm.SugarRecord;

import java.util.LinkedList;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableMaybeObserver;
import mnix.mobilecloud.domain.client.MachineClient;
import mnix.mobilecloud.file.FileUtils;
import mnix.mobilecloud.network.NetworkManager;
import mnix.mobilecloud.repository.client.MachineClientRepository;
import mnix.mobilecloud.web.WebServer;
import mnix.mobilecloud.web.WebSocket;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_SETTINGS = 1;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 2;

    private NetworkManager networkManager;
    private LinkedList<String> logs = new LinkedList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MachineClientRepository.setUniqueIdentifier();
        init();
    }


    boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || !Settings.System.canWrite(this))) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_ACCESS_COARSE_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_WRITE_SETTINGS:
            case REQUEST_CODE_ACCESS_COARSE_LOCATION:
                init();
                break;
        }
    }

    private void init() {
        if (!checkPermissions()) {
            return;
        }
        networkManager = new NetworkManager(this);
        networkManager.connectOrCreateAp().subscribe(new DisposableMaybeObserver<MachineRole>() {
            @Override
            public void onSuccess(MachineRole machineRole) {
                Log.e("MOBILE CLOUD", "init " + machineRole);
                if (machineRole == MachineRole.MASTER) {
                    initMaster();
                } else if (machineRole == MachineRole.SLAVE) {
                    initSlave();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                this.dispose();
            }
        });
    }

    private void initMaster() {
        MachineClientRepository.updateRole(MachineRole.MASTER);
        new WebServer(getApplicationContext());
        new WebSocket();
    }

    private void initSlave() {
        MachineClientRepository.updateRole(MachineRole.SLAVE);
        new WebServer(getApplicationContext());
    }

    public void addLog(String log) {
        logs.add(0, log + "\n");
        if (logs.size() > 10) {
            logs.removeLast();
        }
        final TextView et = (TextView) findViewById(R.id.logs);
        Observable.fromIterable(logs).reduce(new BiFunction<String, String, String>() {
            @Override
            public String apply(@NonNull String a, @NonNull String b) throws Exception {
                return a + b;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String e) throws Exception {
                et.setText(e);
            }
        });
    }

    public void updateWifiInfo(String log) {
        TextView tv = (TextView) findViewById(R.id.wifiInfo);
        tv.setText(log);
    }

    public void apEnable(View view) {
        networkManager.enableAp();
    }

    public void wifiEnable(View view) {
        networkManager.enableWifi();
    }
}
