package wifi.zbf.com.zbf.wifi;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class Main2Activity extends AppCompatActivity
{
    WifiAdmin wifiAdmin;
    List<ScanResult> list_wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Main2ActivityPermissionsDispatcher.needLocationWithCheck(this);
        viewInit();
    }

    private void viewInit() {
    }

    private void wifiInit() {
        wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        wifiAdmin.startScan();
        list_wifi = wifiAdmin.getWifiList();
        Log.e("zbf", list_wifi.toString());
        WifiConfiguration configuration = wifiAdmin.createWifiCfg("zbf-123456", "hsrg8888", 3);
        wifiAdmin.createWifiAP(configuration, true);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Method method = connectivityManager.getClass().getMethod("startTethering",);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void needLocation() {
        wifiInit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Main2ActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
