package wifi.zbf.com.zbf.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import wifi.zbf.com.zbf.wifi.sockets.Client;
import wifi.zbf.com.zbf.wifi.sockets.ConnectThread;
import wifi.zbf.com.zbf.wifi.sockets.ListenerThread;
import wifi.zbf.com.zbf.wifi.sockets.Server;

import static wifi.zbf.com.zbf.wifi.PublicStatics.DEVICE_CONNECTED;
import static wifi.zbf.com.zbf.wifi.PublicStatics.DEVICE_CONNECTING;
import static wifi.zbf.com.zbf.wifi.PublicStatics.GET_MSG;
import static wifi.zbf.com.zbf.wifi.PublicStatics.SEND_MSG_ERROR;
import static wifi.zbf.com.zbf.wifi.PublicStatics.SEND_MSG_SUCCSEE;


@RuntimePermissions
public class Main2Activity extends AppCompatActivity
{

    WifiAdmin wifiAdmin;
    List<ScanResult> list_wifi;
    protected Bundle savedInstanceState;

    TextView tv_status;
    TextView tv_wifiConnect;

    EditText et_msg;

    //通信端口号
    private final int PORT = 62014;

    private WifiAPBroadcastReceiver wifiAPBroadcastReceiver;

    //    public static ConnectThread connectThread;
//    private ListenerThread listenerThread;
    private Server listenerThread;
    private Client connectThread;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();
            String text = null;
            switch (msg.what)
            {
                case DEVICE_CONNECTING:                                                     //设备开始连接
//                    connectThread = new ConnectThread(listenerThread.getSocket(), handler);
//                    connectThread.start();
                    connectThread = new Client(wifiAdmin.getGateWay(), PORT, handler);

                    text = "开启通信线程";
                    break;
                case DEVICE_CONNECTED:                                                      //设备连接成功
                    text = "设备连接成功,IP：" + wifiAdmin.getConnectedIP();
                    break;
                case SEND_MSG_SUCCSEE:                                                      //发送消息成功
                    text = "发送消息成功:" + bundle.getString("MSG");
                    break;
                case SEND_MSG_ERROR:                                                        //发送消息失败
                    text = "发送消息失败:" + bundle.getString("MSG");
                    break;
                case GET_MSG:                                                               //收到消息
                    text = "收到来自" + bundle.get("IP") + "消息:" + bundle.getString("MSG") + "时间:" + System.currentTimeMillis();
                    break;
            }
            Toast.makeText(Main2Activity.this, text, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
    private void receiverInit()
    {
        wifiAPBroadcastReceiver = new WifiAPBroadcastReceiver(wifiAdmin)
        {
            @Override
            public void onWifiApEnabled(String wifiStates)
            {
                tv_status.setText(wifiStates);
                if (wifiStates.equals("已开启"))
                {
/*                    if (listenerThread == null)
                    {
                        //启动监听
                        listenerThread = new ListenerThread(PORT, handler);
                        listenerThread.start();
                    }*/
                    listenerThread = new Server(PORT);
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            listenerThread.run();
                        }
                    }).start();

                    Log.e("zbf", "ipAdress:" + wifiAdmin.getWifiApIpAddress());

                }
                if (wifiStates.equals("已关闭"))
                {
                    if (listenerThread != null)
                    {
                        listenerThread.close();
                        listenerThread = null;
                    }
                }
            }

            @Override
            public void onWifiListResult(List<ScanResult> results)
            {
                list_wifi = results;
            }

            @Override
            public void onWifiState(String states)
            {
                tv_wifiConnect.setText(states);
                if (states.startsWith("已连接到网络:wireless-znsx-5B") || states.startsWith("已连接到网络:" + "\"" + "wireless-znsx-5B" + "\""))
                {
/*                    if (listenerThread == null)
                    {
                        //启动监听
                        listenerThread = new ListenerThread(PORT, handler);
                        listenerThread.start();
                    }*/
                    handler.sendEmptyMessage(DEVICE_CONNECTING);
                    wifiAdmin.getGateWay();
                }
            }

            @Override
            public void onWifiConnecting(String states)
            {
                tv_wifiConnect.setText(states);
            }
        };
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(wifiAPBroadcastReceiver, wifiAPBroadcastReceiver.getFilter());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(wifiAPBroadcastReceiver);
    }

    private void viewInit()
    {
        tv_status = (TextView) findViewById(R.id.textView);
        tv_wifiConnect = (TextView) findViewById(R.id.textView2);
        et_msg = (EditText) findViewById(R.id.et_msg);

    }

    public void onClick(View view)
    {
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
                Main2ActivityPermissionsDispatcher.needLocationWithCheck(this);
                wifiAdmin.startScan();
                break;
            case R.id.button4:                  //连接热点
                connect2Hot();
                break;
            case R.id.btn_send:                     //发送消息
                String sendText = et_msg.getText().toString() + "";
                connectThread.sendMessage(sendText);
//                connectThread.sendData(sendText);
                break;
        }
    }

    /**
     * 连接到热点
     */
    private void connect2Hot()
    {
        if (list_wifi != null)
        {
            for (ScanResult result : list_wifi)
            {
                if (result.SSID.equals("\"" + "wireless-znsx-5B" + "\"") || result.SSID.equals("wireless-znsx-5B"))
                {
                    WifiConfiguration config = wifiAdmin.isExsits(result.SSID);
                    if (config == null)
                    {
                        config = wifiAdmin.createConnectWifiCfg(result, "Y690H99Z5C");
                        wifiAdmin.addNetwork(config);
                    } else
                    {
                        wifiAdmin.addNetwork(config);
                    }
                    return;
                }
            }
        }
    }

    /**
     * 初始化热点
     */
    private void wifiInit()
    {
        WifiConfiguration configuration = wifiAdmin.createWifiCfg("wireless-znsx-5B", "Y690H99Z5C", 3);
        if (!wifiAdmin.openWifiAP(configuration))
        {
            tv_status.setText("开启失败");
        }

        Log.e("zbf", wifiAdmin.getWifiInfo().toString());


    }


    @NeedsPermission(Manifest.permission.WRITE_SETTINGS)
    void needSettings()
    {
        wifiInit();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void needLocation()
    {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Main2ActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Main2ActivityPermissionsDispatcher.onActivityResult(this, requestCode);
    }

}
