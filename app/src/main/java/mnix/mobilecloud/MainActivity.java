package mnix.mobilecloud;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import mnix.mobilecloud.communication.client.MachineClientCommunication;
import mnix.mobilecloud.domain.client.MachineClient;
import mnix.mobilecloud.domain.client.SegmentClient;
import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.domain.server.MachineServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.network.NetworkManager;
import mnix.mobilecloud.repository.client.MachineClientRepository;
import mnix.mobilecloud.repository.server.MachineServerRepository;
import mnix.mobilecloud.util.Util;
import mnix.mobilecloud.web.client.WebServerClient;
import mnix.mobilecloud.web.server.WebServerServer;
import mnix.mobilecloud.web.socket.WebSocketServer;
import rx.Observer;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_SETTINGS = 1;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 2;

    private NetworkManager networkManager;
    private WebServerServer webServerServer;
    private WebServerClient webServerClient;
    private WebSocketServer webSocketServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        init();
        networkManager = new NetworkManager(this);
        MachineClientRepository.update();
        initMaster();
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
        MachineClientRepository.update();
        networkManager = new NetworkManager(this);
        networkManager.connectOrCreateAp().subscribe(new Observer<MachineRole>() {
            @Override
            public void onCompleted() {
                Util.log(this.getClass(), "update onComplete");
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(MachineRole machineRole) {
                Util.log(this.getClass(), "update onSuccess", machineRole.toString());
                if (machineRole == MachineRole.MASTER) {
                    initMaster();
                } else if (machineRole == MachineRole.SLAVE) {
                    initSlave();
                }
            }
        });
    }

    private void initMaster() {
        dispose();
        MachineClientRepository.updateRole(MachineRole.MASTER);
        MachineServer machineServer = new MachineServer(MachineClientRepository.get());
        MachineServerRepository.update(machineServer);
        webSocketServer = new WebSocketServer();
        webServerServer = new WebServerServer(getApplicationContext(), webSocketServer);
        webServerClient = new WebServerClient(getApplicationContext(), webSocketServer);
    }

    private void initSlave() {
        dispose();
        MachineClientRepository.updateRole(MachineRole.SLAVE);
        webServerClient = new WebServerClient(getApplicationContext());
        MachineClientCommunication machineCommunication = new MachineClientCommunication(getApplicationContext());
        machineCommunication.updateMachine();
    }

    private void dispose() {
        if (webServerServer != null) {
            webServerServer.stop();
            webServerServer = null;
        }
        if (webServerClient != null) {
            webServerClient.stop();
            webServerClient = null;
        }
        if (webSocketServer != null) {
            webSocketServer.stop();
            webSocketServer = null;
        }

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

    public void init(View view) {
        init();
    }


    public void clear(View view) {
        Util.log(this.getClass(), "clear");
        MachineServer.deleteAll(MachineServer.class);
        SegmentServer.deleteAll(SegmentServer.class);
        FileServer.deleteAll(FileServer.class);
//        MachineClient.deleteAll(MachineClient.class);
        SegmentClient.deleteAll(SegmentClient.class);
        MachineClientRepository.update();
        MachineServer machineServer = new MachineServer(MachineClientRepository.get());
        MachineServerRepository.update(machineServer);
    }
    public void clearClient(View view) {
        Util.log(this.getClass(), "clearClient");
//        MachineServer.deleteAll(MachineServer.class);
//        SegmentServer.deleteAll(SegmentServer.class);
//        FileServer.deleteAll(FileServer.class);
        MachineClient.deleteAll(MachineClient.class);
        SegmentClient.deleteAll(SegmentClient.class);
        MachineClientRepository.update();
    }

    public void clearServer(View view) {
        Util.log(this.getClass(), "clearServer");
        MachineServer.deleteAll(MachineServer.class);
        SegmentServer.deleteAll(SegmentServer.class);
        FileServer.deleteAll(FileServer.class);
        MachineServer machineServer = new MachineServer(MachineClientRepository.get());
        MachineServerRepository.update(machineServer);
//        MachineClient.deleteAll(MachineClient.class);
//        SegmentClient.deleteAll(SegmentClient.class);
    }
}
