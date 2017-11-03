package wifi.zbf.com.zbf.wifi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.Executors;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class Main2Activity extends AppCompatActivity
{

    WifiAdmin wifiAdmin;
    List<ScanResult> list_wifi;
    protected Bundle savedInstanceState;

    TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main2);
        Main2ActivityPermissionsDispatcher.needLocationWithCheck(this);
//        Main2ActivityPermissionsDispatcher.needSettingsWithCheck(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewInit();
    }

    private void viewInit() {
        tv_status = (TextView) findViewById(R.id.textView);


    }

    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button:                   //开启热点
                Main2ActivityPermissionsDispatcher.needSettingsWithCheck(this);
                break;
            case R.id.button2:                  //关闭热点
                if (wifiAdmin.closeWifiAp())
                {
                    tv_status.setText("已关闭");
                } else
                {
                    tv_status.setText("关闭失败");
                }
                break;
        }
    }

    /**
     * 初始化热点
     */
    private void wifiInit() {
        wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        wifiAdmin.startScan();
        list_wifi = wifiAdmin.getWifiList();
        Log.e("zbf", list_wifi.toString());
        WifiConfiguration configuration = wifiAdmin.createWifiCfg("wireless-znsx-5B", "Y690H99Z5C", 3);
        if (wifiAdmin.openWifiAP(configuration))
        {
            tv_status.setText("开启成功");
//            Toast.makeText(Main2Activity.this, "开启成功", Toast.LENGTH_SHORT).show();
        } else
        {
            tv_status.setText("开启失败");
//            Toast.makeText(Main2Activity.this, "开启失败", Toast.LENGTH_SHORT).show();
        }

        Log.e("zbf", wifiAdmin.getWifiInfo().toString());


    }


    @NeedsPermission(Manifest.permission.WRITE_SETTINGS)
    void needSettings() {
        wifiInit();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void needLocation() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Main2ActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Main2ActivityPermissionsDispatcher.onActivityResult(this, requestCode);
    }

}
