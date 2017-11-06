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
    TextView tv_wifiConnect;

    private WifiAPBroadcastReceiver wifiAPBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main2);
        Main2ActivityPermissionsDispatcher.needLocationWithCheck(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        wifiAdmin = new WifiAdmin(this);
        receiverInit();
        viewInit();
    }

    /**
     * 广播初始化
     */
    private void receiverInit() {
        wifiAPBroadcastReceiver = new WifiAPBroadcastReceiver(wifiAdmin)
        {
            @Override
            public void onWifiApEnabled(String wifiStates) {
                tv_status.setText(wifiStates);
            }

            @Override
            public void onWifiListResult(List<ScanResult> results) {
                list_wifi = results;
            }

            @Override
            public void onWifiState(String states) {
                tv_wifiConnect.setText(states);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiAPBroadcastReceiver, wifiAPBroadcastReceiver.getFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiAPBroadcastReceiver);
    }

    private void viewInit() {
        tv_status = (TextView) findViewById(R.id.textView);
        tv_wifiConnect = (TextView) findViewById(R.id.textView2);


    }

    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button:                   //开启热点
                Main2ActivityPermissionsDispatcher.needSettingsWithCheck(this);
                break;
            case R.id.button2:                  //关闭热点
                if (!wifiAdmin.closeWifiAp())
                {
                    tv_status.setText("关闭失败");
                }
                break;
            case R.id.button3:                  //搜索热点
                wifiAdmin.startScan();
                break;
            case R.id.button4:                  //连接热点
                for (ScanResult result : list_wifi)
                {
                    if (result.BSSID.equals("HSRG2"))
                    {
                        //加密方式
                        String capabliities = result.capabilities;

                        WifiConfiguration configuration = wifiAdmin.isExsits("HSRG2");
                        if (configuration == null)
                        {

                        } else
                        {

                        }
                    }
                }
                break;
        }
    }

    /**
     * 初始化热点
     */
    private void wifiInit() {
        WifiConfiguration configuration = wifiAdmin.createWifiCfg("wireless-znsx-5B", "Y690H99Z5C", 3);
        if (!wifiAdmin.openWifiAP(configuration))
        {
            tv_status.setText("开启失败");
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
