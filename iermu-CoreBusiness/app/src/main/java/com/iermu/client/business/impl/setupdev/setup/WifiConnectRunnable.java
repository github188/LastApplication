package com.iermu.client.business.impl.setupdev.setup;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

import com.cms.iermu.cms.CmsMenu;
import com.iermu.client.ErmuApplication;
import com.iermu.client.business.impl.setupdev.WifiNetworkManager;
import com.iermu.client.util.Logger;

/**
 * Wifi连接处理线程
 *
 * Created by wcy on 15/8/10.
 */
public class WifiConnectRunnable extends Thread {

    private static final int MAX_TIMEOUT = 20000;
    private WifiNetworkManager wifiManager;
    private WifiReceiver receiver;
    private WifiConnectListener listener;
    private String  ssid;       //
    private String  bssid;      //
    private String  password;   //
    private int     wifiType;   //
    private long    startTime;
    private boolean quitWifiConnect;

    public WifiConnectRunnable(String ssid, String bssid, String pwd, int wifiType) {
        this.ssid       = ssid;
        this.bssid      = bssid;
        this.password   = pwd;
        this.wifiType   = wifiType;

        Application context = ErmuApplication.getContext();
        this.wifiManager = WifiNetworkManager.getInstance(context);
        this.receiver = new WifiReceiver();

        String action       = WifiManager.NETWORK_STATE_CHANGED_ACTION;
        IntentFilter intent = new IntentFilter(action);
        context.registerReceiver(receiver, intent);
    }

    public WifiConnectRunnable setOnWifiConnectListener(WifiConnectListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        unRegisterReceiver();
    }

    @Override
    public void run() {
        super.run();

        boolean bWifiOK = false;
        boolean ipcAP   = CmsMenu.isIpcAP(ssid, bssid); //判断当前连接Wifi是否是设备Ap
        startTime       = System.currentTimeMillis();
        String curSSID  = wifiManager.getSSID();
        if(ipcAP && curSSID.contains(ssid) ) {  //检测当前连接Wifi, 已经为指定设备Ap
            onWifiConnected(true);
            return;
        }

        Logger.i("WifiConnect", "start conn ssid:" + ssid + ", wifiType:" + wifiType + ", pwd=" + password);
        while(!bWifiOK){
            Logger.i("WifiConnect", "connectWifiNetwork ssid:"+ssid+", wifiType:"+wifiType+", pwd="+password+" ipcAP:"+ipcAP);
            bWifiOK = wifiManager.connectWifiNetwork(ssid, password, wifiType, ipcAP);
            quitWifiConnect = (bWifiOK || System.currentTimeMillis()-startTime>MAX_TIMEOUT);
            if(quitWifiConnect) {
                break;
            }
            if(!bWifiOK) {
                Logger.i("WifiConnect", "closeWifi");
                wifiManager.closeWifi();

                Logger.i("WifiConnect", "openWifi");
                wifiManager.openWifi();
            }
        }

        Logger.i("WifiConnect", "end conn ssid:" + ssid + ", wifiType:" + wifiType + ", pwd=" + password);
        boolean b = connectedSSID();
        if(b) {//直接得到成功的结果
            onWifiConnected(true);
        } else if(System.currentTimeMillis()-startTime>MAX_TIMEOUT) {//超时,未成功
            //超时情况处理:
            //-> 仅提示因为超时导致的连接未成功.(成功 | 未成功)
            onWifiConnected(b);
        }
    }

    private boolean connectedSSID() {
        String curSSID = (wifiManager!=null) ? wifiManager.getSSID() : "";
        if(curSSID == null) {
            curSSID = "";
        }
        return curSSID.contains(ssid);
    }

    private void unRegisterReceiver() {
        if(receiver == null) return;
        synchronized (WifiConnectRunnable.class) {
            if(receiver == null) return;
            ErmuApplication.getContext().unregisterReceiver(receiver);
            this.receiver = null;
        }
    }

    private void onWifiConnected(boolean success) {
        unRegisterReceiver();

        if(listener == null) return;
        listener.onWifiConnected(success);
    }

    /**
     * Wifi连接状态事件
     */
    interface WifiConnectListener {

        /**
         * Wifi状态
         * @param success
         */
        public void onWifiConnected(boolean success);

    }

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                String key       = WifiManager.EXTRA_NETWORK_INFO;
                NetworkInfo info = intent.getParcelableExtra(key);
                if(info.isConnected()) {
                    String curSSID = (wifiManager!=null) ? wifiManager.getSSID() : "";
                    Logger.i("WifiReceiver", "Wifi is connected: "+ info.getExtraInfo()+" currentSSID:"+curSSID);
                    boolean b = connectedSSID();
                    if(b) {//直接得到成功的结果
                        unRegisterReceiver();
                        onWifiConnected(true);
                    } else if(quitWifiConnect) {//退出连接程序
                        unRegisterReceiver();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SystemClock.sleep(5000);
                                boolean b = connectedSSID();
                                onWifiConnected(b);
                            }
                        }).start();
                    }
                }
            } else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                String key = ConnectivityManager.EXTRA_NETWORK_INFO;
                NetworkInfo info = intent.getParcelableExtra(key);
                if(info.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                    //通知Wifi连接失败 // Wifi is disconnected
                    if(quitWifiConnect) {//超时,未成功
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                onWifiConnected(false);
                            }
                        }).start();
                    }
                    Log.d("WifiReceiver", "Wifi is disconnected: " + String.valueOf(info));
                }
            }
        }
    }

}
