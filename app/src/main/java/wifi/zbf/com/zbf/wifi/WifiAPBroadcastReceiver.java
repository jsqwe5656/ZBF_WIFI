package wifi.zbf.com.zbf.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;
import java.util.Scanner;

/**
 * Wifi AP BroadReciver
 * wifi 热点 广播
 * <p>
 * Created by mayubao on 2016/11/4.
 * Contact me 345269374@qq.com
 */
public abstract class WifiAPBroadcastReceiver extends BroadcastReceiver
{

    public static final String TAG = WifiAPBroadcastReceiver.class.getSimpleName();
    private WifiAdmin wifiAdmin;

    public WifiAPBroadcastReceiver(WifiAdmin wifiAdmin) {
        this.wifiAdmin = wifiAdmin;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_WIFI_AP_STATE_CHANGED))
        { //Wifi AP state changed
            //便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.i(TAG, "Wifi Ap state--->>>" + state);
            if (WifiManager.WIFI_STATE_ENABLED == state % 10)
            {
                onWifiApEnabled("已开启");
            } else if (WifiManager.WIFI_STATE_ENABLING == state % 10)
            {
                onWifiApEnabled("正在开启");
            } else if (WifiManager.WIFI_STATE_DISABLED == state % 10)
            {
                onWifiApEnabled("已关闭");
            } else if (WifiManager.WIFI_STATE_DISABLING == state % 10)
            {
                onWifiApEnabled("正在关闭");
            }
        } else if (action.equals(ACTION_SCAN_RESULTS_AVAILABLE_ACTION))
        {
            List<ScanResult> results = wifiAdmin.getWifiList();
            onWifiListResult(results);
            Log.i(TAG, results.toString());
        } else if (action.equals(ACTION_WIFI_STATE_CHANGED_ACTION))
        {
            int state = intent.getIntExtra("wifi_state", 1000);
            Log.e(TAG, "wifi_state:" + state);
            switch (state)
            {
                case WifiManager.WIFI_STATE_DISABLED:
                    onWifiState("WIFI已关闭");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    onWifiState("WIFI正在关闭");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    onWifiState("WIFI已开启");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    onWifiState("WIFI正在开启");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    onWifiState("WIFI未知");
                    break;
            }
        }


    }

    /**
     * 热点状态
     */
    public abstract void onWifiApEnabled(String wifiStates);

    /**
     * WIFI搜索列表
     */
    public abstract void onWifiListResult(List<ScanResult> results);

    /**
     * WIFI状态
     */
    public abstract void onWifiState(String states);

    //WIFI AP state action
    public static final String ACTION_WIFI_AP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    //搜索完成返回列表
    public static final String ACTION_SCAN_RESULTS_AVAILABLE_ACTION = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
    //WIFI状态改变
    public static final String ACTION_WIFI_STATE_CHANGED_ACTION = WifiManager.WIFI_STATE_CHANGED_ACTION;

    /**
     * 监听规则
     */
    public IntentFilter getFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_WIFI_AP_STATE_CHANGED);
        intentFilter.addAction(ACTION_SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(ACTION_WIFI_STATE_CHANGED_ACTION);
        return intentFilter;
    }


}
