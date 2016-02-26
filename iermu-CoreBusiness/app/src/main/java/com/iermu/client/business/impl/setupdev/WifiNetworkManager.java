package com.iermu.client.business.impl.setupdev;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.iermu.client.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wifi网络管理
 *
 *  1.开启自动扫描Wifi
 *    实时返回扫描结果
 *  2.结束Wifi自动扫描
 *  3.获取当前连接Wifi信息
 *  4.创建指定Wifi(ssid\pwd),并连接
 *  5.连接指定Wifi(ssid)
 *  6.开启Wifi
 *  7.关闭Wifi
 *
 * Created by wcy on 15/8/1.
 */
public class WifiNetworkManager {

    private static WifiNetworkManager mInstance;
    private WifiManager wifiManager;
    private List<WifiConfiguration> wifiConfigurations;
    private List<ScanResult> scanWifiList;
    private Map<String, ScanResult> cacheMap;
    private int frequency;  //当前连接Wifi frequency
    private String capabilities;

    private WifiNetworkManager(Context context) {
        this.scanWifiList   = new ArrayList<ScanResult>();
        this.cacheMap       = new HashMap<String, ScanResult>();
        this.wifiManager    = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);//取得WifiManager对象
        //mWifiManager.startScan();
        this.wifiConfigurations = wifiManager.getConfiguredNetworks();
    }

    /**
     * 获取Wifi网络管理类实例
     * @param context
     * @return
     */
    public static WifiNetworkManager getInstance(Context context) {
        if(mInstance == null) {
            synchronized (WifiNetworkManager.class) {
                if(mInstance == null) {
                    mInstance = new WifiNetworkManager(context);
                }
            }
        }
        return mInstance;
    }

    /**  开启Wifi */
    public boolean openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            Log.i("WifiNetWorkManager", "setWifiEnabled.....start");
            wifiManager.setWifiEnabled(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("WifiNetWorkManager", "setWifiEnabled.....end");
        }
        return wifiManager.isWifiEnabled();
    }

    /** 关闭Wifi */
    public void closeWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测当前Wifi连接是否开启成功
     * @return
     */
    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    /**
     * 检测当前Wifi连接正在开启状态
     * @return
     */
    public boolean isWifiEnabling() {
        int state = wifiManager.getWifiState();
        return state == WifiManager.WIFI_STATE_ENABLING;
    }

    /**
     * 获取当前Wifi Mac地址
     * @return
     */
    public String getMacAddress() {
        WifiInfo info = wifiManager.getConnectionInfo();
        return (info == null) ? "" : info.getMacAddress();
    }

    /**
     * 获取当前Wifi 接入点地址
     * @return
     */
    public String getBSSID() {
        WifiInfo info = wifiManager.getConnectionInfo();
        return (info == null) ? "" : info.getBSSID();
    }

    /**
     * 获取当前Wifi SSID
     * @return
     */
    public String getSSID() {
        WifiInfo info = wifiManager.getConnectionInfo();
        return (info == null) ? "" : info.getSSID();
    }

    /**
     * 获取当前Wifi Frequency
     * @return
     */
    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public int getFrequency() {
        WifiInfo info = wifiManager.getConnectionInfo();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return info.getFrequency();
        }
        return frequency;
    }

//    /**
//     * 获取当前Wifi capabilities
//     * @return
//     */
//    public String getCapabilities() {
//        //WifiInfo info = wifiManager.getConnectionInfo();
//        return capabilities;
//    }

    public String getCapabilities() {
        WifiInfo info = wifiManager.getConnectionInfo();
        List<WifiConfiguration> wifiConfigList = wifiManager.getConfiguredNetworks();// 得到配置好的网络连接
        if(wifiConfigList!=null) {
            for (WifiConfiguration wifiConfiguration : wifiConfigList) {
                String configSSid = wifiConfiguration.SSID;//配置过的SSID
                configSSid = configSSid.replace("\"", "");
                //当前连接SSID
                String currentSSid =info.getSSID();
                currentSSid = currentSSid.replace("\"", "");
                //比较networkId，防止配置网络保存相同的SSID
                if (currentSSid.equals(configSSid)&&info.getNetworkId()==wifiConfiguration.networkId) {
                    return getSecurity(wifiConfiguration);
                }
            }
        }
        return SECURITY_WEP;
    }

    static final String SECURITY_OPEN = "open";
    static final String SECURITY_WEP = "wep";
    static final String SECURITY_WPA = "wpa";
    static final String SECURITY_EAP = "eap";
    static String getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_WPA;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_OPEN;
    }

    /**
     * 判断当前Wifi 是否为5G
     * @return
     */
    public boolean is5GHz() {
        int frequency = getFrequency();
        return frequency > 4900 && frequency < 5900;
    }

    /**
     * 判断Wifi 是否为5G
     * @param freq
     * @return
     */
    public static boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }

    /**
     * 获取当前Wifi SSID (取出前后缀 " ")
     * @return
     */
    public String getSSIDStr() {
        WifiInfo info = wifiManager.getConnectionInfo();
        if(info == null || TextUtils.isEmpty(info.getSSID())) {
            return "";
        } else {
            String SSID = info.getSSID();
            boolean b = SSID.startsWith("\"");
            boolean b1 = SSID.endsWith("\"");
            int length = SSID.length();
            return SSID.substring(b?1:0, b1?length-1:length );
        }
    }

    /**
     * 获取当前Wifi Ip地址
     * @return
     */
    public int getIpAddress() {
        WifiInfo info = wifiManager.getConnectionInfo();
        return (info == null) ? 0 : info.getIpAddress();
    }

    /**
     * 开始扫描Wifi
     */
    public void startScan() {
        startScan(null);
    }



    /**
     * 开始扫描Wifi
     * @param listener
     */
    public void startScan(OnWifiScanListener listener) {
        String currentSSID = getSSIDStr();
        wifiManager.startScan();

        // 得到扫描结果
        List<ScanResult> result = wifiManager.getScanResults();
        if(result==null) return;
        synchronized (WifiNetworkManager.class) {
            scanWifiList.clear();//清除上次的扫描列表(保留每次最新的结果)
            for(int i=0; i<result.size(); i++){
                ScanResult scanResult = result.get(i);
                String SSID = scanResult.SSID;
                cacheMap.put(SSID, scanResult);
                boolean haveSameSSID = false;

                if(currentSSID.equals(SSID)) {
                    frequency = scanResult.frequency;
                    capabilities = scanResult.capabilities;
                }
                for(int j=0; j<scanWifiList.size(); j++){
                    if(SSID.equals(scanWifiList.get(j).SSID)){
                        haveSameSSID = true;
                        break;
                    }
                }
                if(haveSameSSID) continue;
                scanWifiList.add(scanResult);
            }
        }

        // 得到配置好的网络连接
        wifiConfigurations = wifiManager.getConfiguredNetworks();
        if(listener != null) {
            List<ScanResult> list = new ArrayList<ScanResult>(Arrays.asList(new ScanResult[scanWifiList.size()]));
            Collections.copy(list, scanWifiList);
            listener.onWifiScanResult(list);
        }
    }

    /**
     * 获取扫描结果列表
     * @return
     */
    public List<ScanResult> getScanWifResult() {
        List<ScanResult> results = wifiManager.getScanResults();
        if(results==null) results = new ArrayList<ScanResult>();

        List<ScanResult> list = new ArrayList<ScanResult>();
        synchronized (WifiNetworkManager.class) {
            scanWifiList.clear();//清除上次的扫描列表(保留每次最新的结果)
            for(int i=0; i<results.size(); i++){
                ScanResult scan = results.get(i);
                String SSID     = scan.SSID;
                cacheMap.put(SSID, scan);
                boolean haveSameSSID = false;

                for(int j=0; j<scanWifiList.size(); j++){
                    if(SSID.equals(scanWifiList.get(j).SSID)){
                        haveSameSSID = true;
                        break;
                    }
                }
                if(haveSameSSID) continue;
                scanWifiList.add(scan);
            }

            list = new ArrayList<ScanResult>(Arrays.asList(new ScanResult[scanWifiList.size()]));
            Collections.copy(list, scanWifiList);
        }
        return list;
    }

    /**
     * 查找指定SSID ScanResult
     * @param ssid
     * @return
     */
    public ScanResult findScanResult(String ssid) {
        ScanResult exist = null;
        synchronized (WifiNetworkManager.class) {
            if(cacheMap.containsKey(ssid)) {
                exist = cacheMap.get(ssid);
            } else {
                exist = findScanWifiList(ssid);
            }

            if(exist == null) {
                startScan();
                SystemClock.sleep(1000);
                exist = findScanWifiList(ssid);
            }
        }
        return exist;
    }

    /**
     * 连接指定SSID配置好的网络()
     * @param ssid Wifi SSID
     */
    public void connectWifi(String ssid) {
        if(wifiConfigurations == null) return;
        int iLen = wifiConfigurations.size();
        for(int i=0; i<iLen; i++){
            WifiConfiguration config = wifiConfigurations.get(i);
            int networkId = config.networkId;
            if(config.SSID.indexOf(ssid) >= 0){// 连接配置好指定ID的网络
                wifiManager.enableNetwork(networkId, true);
            }
        }
    }

    /**
     * 删除指定网络
     * @param SSID
     */
    public void deletedWifi(String SSID) {
        WifiConfiguration configuration = findExistWifi(SSID);
        if (configuration != null) {
            wifiManager.removeNetwork(configuration.networkId);
            wifiManager.disableNetwork(configuration.networkId);
        }
    }

    /**
     * 判断wifi是否存在
     * @param ssid Wifi SSID
     * @return
     */
    public boolean isExsits(String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : existingConfigs) {
            if (config.SSID.indexOf(ssid) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找已经存在的Wifi配置信息
     * @param ssid Wifi SSID
     * @return
     */
    public WifiConfiguration findExistWifi(String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration config : existingConfigs) {
                if (config.SSID.indexOf(ssid) >= 0) {
                    return config;
                }
            }
        }
        return null;
    }

    /**
     * 连接指定Wifi网络(根据配置信息自动创建Wifi)
     * @param SSID          Wifi SSID
     * @param Password      Wifi 密码
     * @param Type          Wifi加密类型
     * @param bRemoveWifi   是否清除原来的网络设置
     * @return
     */
    public boolean connectWifiNetwork(String SSID, String Password, int Type, boolean bRemoveWifi) {
        WifiConfiguration info = createWifiInfo(SSID, Password, Type, bRemoveWifi);
        return addNetWork(info);
    }

    /**
     * 添加一个网络并连接
     * @param configuration
     * @return
     */
    public boolean addNetWork(WifiConfiguration configuration) {
        int networkId = wifiManager.addNetwork(configuration);
        boolean isSuccess = wifiManager.enableNetwork(networkId, true);
        Logger.i("WifiNetWorkManager", "add net ret " + networkId + ", enable " + isSuccess);
        return isSuccess;
    }

    /**
     * 创建Wifi配置
     *
     * @param SSID
     * @param Password
     * @param Type
     * @param bRemoveWifi 是否清除原来的网络设置
     * @return
     */
    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type, boolean bRemoveWifi) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = findExistWifi(SSID);
        if (tempConfig != null) {
            if(Type==4 || Type==2 || !bRemoveWifi){
                config = tempConfig;
                return config;
            }
            else {
                wifiManager.removeNetwork(tempConfig.networkId);
            }
        }

        if (Type == 1) {// WIFICIPHER_NOPASS
			/*config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;*/

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

        } else if (Type == 2) { // WIFICIPHER_WEP
			/*config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;*/

            //readWepConfig();

			/*config.hiddenSSID = true;
			config.status = WifiConfiguration.Status.DISABLED;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.priority = 40;
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.wepTxKeyIndex = 0;*/
        } else if (Type == 3) {// WIFICIPHER_WPA
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
        } else if (Type == 4) { // eap
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
            // config.enterpriseConfig.setIdentity(identity);
            return config;
        }
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }


    private synchronized ScanResult findScanWifiList(String ssid) {
        ScanResult exist = null;
        for(int i=0; i<scanWifiList.size(); i++) {
            ScanResult result = scanWifiList.get(i);
            if(result.SSID.indexOf(ssid) >= 0) {
                exist = result;
                break;
            }
        }
        return exist;
    }

    /**
     * Wi-Fi扫描监听器
     */
    interface OnWifiScanListener {

        public void onWifiScanResult(List<ScanResult> list);

    }

}
