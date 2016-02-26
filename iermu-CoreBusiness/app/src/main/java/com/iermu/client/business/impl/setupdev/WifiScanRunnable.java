package com.iermu.client.business.impl.setupdev;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.SystemClock;

import com.cms.iermu.cms.CmsMenu;
import com.iermu.client.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描Wifi列表
 *
 * Created by wcy on 15/7/21.
 */
public class WifiScanRunnable extends Thread {

    private Context context;
    private WifiNetworkManager wifiManager;
//    private WifiAdmin wifiAdmin;
//    private int wifiType;
    private List<ScanResult> wifiList;
    private OnScanCamDevListener listener;
    private boolean isInterrupted = false;

    public WifiScanRunnable(Context context) {
        this.context = context;
        this.isInterrupted = false;
        this.wifiList = new ArrayList<ScanResult>();
        this.wifiManager = WifiNetworkManager.getInstance(context);
//        this.wifiAdmin = new WifiAdmin(context);
    }

    public WifiScanRunnable setListener(OnScanCamDevListener listener) {
        this.listener = listener;
        return this;
    }

    public synchronized void interrupt() {
        super.interrupt();
        this.isInterrupted = true;
    }

    @Override
    public void run() {
        // 扫描耳目设备
        long lSt        = System.currentTimeMillis();
        int iT          = 9000;
        while(System.currentTimeMillis()-lSt < iT && !isInterrupted){
            // 检测是否开启wifi
//            if(wifiAdmin.checkState() != WifiManager.WIFI_STATE_ENABLED){ // wifi关闭
            if( !wifiManager.isWifiEnabled() ) {
                onWifiClose();
                return;
            }

//            wifiType = 3;
            //开始扫描网络
//            wifiAdmin.startScan();
//            SystemClock.sleep(500);
//            wifiAdmin.startScan();
//            SystemClock.sleep(500);
//            wifiAdmin.startScan();

            wifiManager.startScan();
            SystemClock.sleep(500);
            wifiManager.startScan();
            SystemClock.sleep(500);
            wifiManager.startScan();

            // 将手机当前连接wifi在列表中第一个显示
            // 扫描结果列表
//            List<ScanResult> list = wifiAdmin.getWifiList();
            List<ScanResult> list = wifiManager.getScanWifResult();
            Logger.i("WifiScanRunnable", "wifi list:\n" + list.toString());
            if(list == null || list.size()<= 0) {
                //没有找到Wifi 继续
                continue;
            }
            for(int i=0; i<list.size(); i++){
                //得到扫描结果
                ScanResult scan = list.get(i);
                if( !CmsMenu.isIpcAP(scan) ) { // ipc ap加密方式
                    if(scan.SSID!=null && !scan.SSID.equals("")) {
                        wifiList.add(scan);
                        //int ifreq = mScanResult.frequency;
                        //if(wifiAdmin.getBSSID()!=null && wifiAdmin.getBSSID().equals(scan.BSSID)){  // 当前wifi加密方式
                        //    wifiType = CmsUtil.getWifiType(scan.capabilities);
                            //if(m_iConnWifiType<0) m_iConnWifiType = wifiType;
                        //}
                    }
                }
            }
            onWifiList();
        }
        Logger.i("WifiScanRunnable", "Wifi scan exit!");
        onWifiList();
    }

    private void onWifiList() {
        if(listener == null) return;
        //返回扫描Wifi结果:
        listener.onWifiList(wifiList);
    }

    //Wifi被关闭
    private void onWifiClose() {
        if(listener == null) return;
        listener.onWifiClose();
    }

}
