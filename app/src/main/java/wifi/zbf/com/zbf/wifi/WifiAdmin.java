package wifi.zbf.com.zbf.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * WIFI管理工具类
 * Created by hs-301 on 2017/10/27.
 */
public class WifiAdmin
{
    private static WifiAdmin wifiAdmin = null;
    private List<WifiConfiguration> mWifiConfiguration; //无线网络配置信息类集合(网络连接列表)
    private List<ScanResult> mWifiList; //检测到接入点信息类 集合
    //描述任何Wifi连接状态
    private WifiInfo mWifiInfo;
    private WifiManager.WifiLock mWifilock; //能够阻止wifi进入睡眠状态，使wifi一直处于活跃状态
    public WifiManager mWifiManager;

    /**
     * 获取该类的实例
     */
    public static WifiAdmin getInstance(Context context) {
        if (wifiAdmin == null)
        {
            wifiAdmin = new WifiAdmin(context);
            return wifiAdmin;
        } else return wifiAdmin;
    }

    public WifiAdmin(Context context) {
        //获取系统Wifi服务   WIFI_SERVICE
        this.mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //获取连接信息
        if (this.mWifiManager != null)
        {
            this.mWifiInfo = this.mWifiManager.getConnectionInfo();
        }
    }

    /**
     * 是否存在网络信息
     *
     * @param str 热点名称
     */
    public WifiConfiguration isExsits(String str) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null)
        {
            for (WifiConfiguration existingConfig : existingConfigs)
            {
//            Log.e("zbf", "isExsits:" + existingConfig.SSID);
                if (existingConfig.SSID.equals("\"" + str + "\""))
                {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    /**
     * 锁定WifiLock，当下载大文件时需要锁定
     **/
    public void acquireWifiLock() {
        this.mWifilock.acquire();
    }

    /**
     * 创建一个WifiLock
     **/
    public void createWifiLock() {
        this.mWifilock = this.mWifiManager.createWifiLock("Test");
    }

    /**
     * 解锁WifiLock
     **/
    public void releaseWifilock() {
        if (mWifilock.isHeld())
        { //判断时候锁定
            mWifilock.acquire();
        }
    }

    /**
     * wifi开启状态
     */
    public boolean getWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 打开Wifi
     **/
    public void openWifi() {
        if (!this.mWifiManager.isWifiEnabled())
        { //当前wifi不可用
            this.mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭Wifi
     **/
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled())
        {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 添加到指定Wifi网络 /切换到指定Wifi网络
     */
    public void addNetwork(WifiConfiguration wf) {
        //连接新的连接
        int netId = mWifiManager.addNetwork(wf);
        Log.e("zbf", "netID:" + netId);
        boolean b = mWifiManager.enableNetwork(netId, true);
        Log.e("zbf", "connect:" + b);

//        mWifiManager.saveConfiguration();
//        mWifiManager.reconnect();
    }

    /**
     * 关闭当前的Wifi网络
     */
    public boolean disconnectCurrentNetwork() {
        if (mWifiManager != null && mWifiManager.isWifiEnabled())
        {
            int netId = mWifiManager.getConnectionInfo().getNetworkId();
            mWifiManager.disableNetwork(netId);
            return mWifiManager.disconnect();
        }
        return false;
    }

    /**
     * 创建WifiConfiguration
     */
    public WifiConfiguration createWifiCfg(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = SSID;
        WifiConfiguration tempConfig = isExsits("\"" + SSID + "\"");
        if (tempConfig != null)
        {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        if (Type == 1) //WIFICIPHER_NOPASS
        {
          /*  config.wepKeys[0] = "";//连接无密码热点时加上这两句会出错
            config.wepTxKeyIndex = 0;*/
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = Password;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = Password;
            config.hiddenSSID = false;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 创建将要链接的wificonfig
     */
    public WifiConfiguration createConnectWifiCfg(ScanResult scan, String Password) {
        WifiConfiguration config = new WifiConfiguration();
        config.hiddenSSID = false;
        config.SSID = scan.SSID;
        config.status = WifiConfiguration.Status.ENABLED;
        if (scan.capabilities.contains("WPA"))
        {
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.preSharedKey = "\"".concat(Password).concat("\"");

        } else if (scan.capabilities.contains("WEP"))
        {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

            config.wepKeys[0] = Password;
            config.wepTxKeyIndex = 0;

        } else
        {
            config.preSharedKey = null;
            config.wepKeys[0] = "\"" + "\"";
            config.wepTxKeyIndex = 0;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.clear();
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
        return config;
    }

    /**
     * 根据wifi信息创建或关闭一个热点
     *
     * @param paramWifiConfiguration WIFI热点配置
     */
    public Boolean openWifiAP(WifiConfiguration paramWifiConfiguration) {
        if (mWifiManager.isWifiEnabled())
        {
            //如果wifi处于打开状态，则关闭wifi,
            mWifiManager.setWifiEnabled(false);
        }
        try
        {
            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            method.invoke(mWifiManager, paramWifiConfiguration, true);
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭热点
     */
    public boolean closeWifiAp() {
        try
        {
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(mWifiManager, null, false);
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取ip地址
     **/
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    /**
     * 获取物理地址(Mac)
     **/
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    /**
     * 获取网络id
     **/
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 获取热点创建状态
     **/
    public int getWifiApState() {
        try
        {
            return (int) (Integer) this.mWifiManager.getClass()
                    .getMethod("getWifiApState", new Class[0])
                    .invoke(this.mWifiManager);
        } catch (Exception ignored)
        {
        }
        return 4;   //未知wifi网卡状态
    }

    /**
     * 获取wifi连接信息
     **/
    public WifiInfo getWifiInfo() {
        return this.mWifiManager.getConnectionInfo();
    }

    /**
     * 得到网络列表
     **/
    public List<ScanResult> getWifiList() {
        mWifiList = mWifiManager.getScanResults();
        return mWifiList;
    }

    /**
     * 查看扫描结果
     **/
    public StringBuilder lookUpScan() {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++)
        {
            localStringBuilder.append("Index_").append(Integer.toString(i + 1)).append(":");
            //将ScanResult信息转换成一个字符串包
            //其中把包括：BSSID、SSID、capabilities、frequency、level
            localStringBuilder.append((mWifiList.get(i)).toString());
            localStringBuilder.append("\n");
        }
        return localStringBuilder;
    }

    /**
     * 开始搜索wifi
     **/
    public void startScan() {
        if (!mWifiManager.isWifiEnabled())
        {
            //开启wifi
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.startScan();
    }

    /**
     * 连接热点
     */
    public void connect(WifiConfiguration configuration) {
        int wcgID = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgID, true);
    }

    /**
     * 连接配置好指定ID的网络
     */
    public void connectExsits() {

    }

    /**
     * 获取连接到热点上的手机ip
     *
     * @return
     */
    public ArrayList<String> getConnectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4)
                {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return connectedIP;
    }

}
