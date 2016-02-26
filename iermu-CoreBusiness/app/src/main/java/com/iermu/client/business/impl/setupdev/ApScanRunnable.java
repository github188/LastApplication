package com.iermu.client.business.impl.setupdev;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.SystemClock;
import android.text.TextUtils;

import com.cms.iermu.cms.CmsMenu;
import com.cms.iermu.cms.CmsUtil;
import com.cms.iermu.cms.cmsNative;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.constant.CamDevType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.ScanDevMode;
import com.iermu.client.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * AP模式扫描摄像机设备
 *
 * Created by wcy on 15/6/22.
 */
public class ApScanRunnable extends Thread {

    private Context context;
    private String baiduUID;//m_baiduUser.strUID
    private String camDevID;
    //private WifiAdmin wifiAdmin;
    private WifiNetworkManager wifiManager;
    //private int wifiType;
    private String apDevPwd;
    private List<CamDev> devList;
    private List<ScanResult> wifiList;
    private OnScanCamDevListener listener;
    private boolean isInterrupted = false;

    public ApScanRunnable(Context context, String baiduUID) {
        this.context        = context;
        this.isInterrupted  = false;
        this.baiduUID       = baiduUID;
        this.apDevPwd       = cmsNative.getIpcApPwd("3");
        this.devList        = new ArrayList<CamDev>();
        this.wifiList       = new ArrayList<ScanResult>();
        this.wifiManager    = WifiNetworkManager.getInstance(context);
        //this.wifiAdmin = new WifiAdmin(context);\
    }

    /**
     * 查找指定设备的ID
     * @param camDevID
     */
    public ApScanRunnable setFindDevID(String camDevID) {
        this.camDevID = camDevID;
        return this;
    }

    public ApScanRunnable setListener(OnScanCamDevListener listener) {
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
        int iT          = TextUtils.isEmpty(camDevID)? 59000 : 9000;
        wifiManager.startScan();
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

            //TODO 测试是否需要扫描3次
            wifiManager.startScan();
            SystemClock.sleep(500);
            wifiManager.startScan();
//            SystemClock.sleep(500);
//            wifiManager.startScan();
//            String bssid = wifiManager.getBSSID();
            String ssid = wifiManager.getSSID();
            List<ScanResult> list = wifiManager.getScanWifResult();
            // 将手机当前连接wifi在列表中第一个显示
            // 扫描结果列表
//            List<ScanResult> list = wifiAdmin.getWifiList();
            //Logger.i("ApScanRunnable", "wifi: count="+list.size()+" \n >>>>" + list.toString());
            if(list == null || list.size()<= 0) {
                //没有找到Wifi 继续
                continue;
            }
            for(int i=0; i<list.size(); i++){
                //得到扫描结果
                ScanResult scan = list.get(i);
                if(CmsMenu.isIpcAP(scan)) { // ipc ap加密方式
                    String scanSSID = TextUtils.isEmpty(scan.SSID) ? "" : scan.SSID;
                    boolean lyyAP = scanSSID.startsWith(CmsUtil.IPC_LYYAP_SSID);

                    CamDev dev = new CamDev();
                    dev.setBSSID(scan.BSSID);
                    dev.setSSID(scan.SSID);
                    dev.setDevType(CamDevType.TYPE_AP);
                    dev.setDevPwd(apDevPwd);
                    dev.setWifiType(CmsUtil.getWifiType(scan.capabilities));
                    dev.setConnectType(lyyAP ? ConnectType.LINYANG : ConnectType.BAIDU);
                    // 检查是否重复
                    Logger.i("ApScanRunnable", "find dev type=" + dev.getDevType() + ", ssid=" + dev.getSSID());
                    if(checkDevOne(dev.getBSSID()) ) {// && m_bScanAp
                        String strTemp = CmsMenu.getDevIDByMac(scan.BSSID, scan.SSID.indexOf(CmsUtil.IPC_AP_SSID_DIRECT)==0);
                        dev.setDevID(strTemp);
                        //TODO start 增加测试代码
                        //if("137893001259".equals(strTemp)
                        //    || "137892923067".equals(strTemp)
                        //    || "137893657563".equals(strTemp)
                        //    || "137893708043".equals(strTemp)) {
                        //    dev.setConnectType(ConnectType.LINYANG);
                        //}
                        //TODO end  增加测试代码
                        if(!TextUtils.isEmpty(camDevID) && strTemp!=null && camDevID.equals(strTemp)) {  // 找到想要配置的设备
                            devList.add(0, dev);
                            onDevList();
                            return;
                        } else {
                            devList.add(dev);
                            onDevList();
                        }
                    }
                } else if(TextUtils.isEmpty(scan.SSID)){
                    if(scan.SSID.equals(ssid)) {//将手机当前连接wifi在列表中第一个显示
                        wifiList.add(0, scan);
                    } else {
                        wifiList.add(scan);
                    }
                    //int ifreq = mScanResult.frequency;
//                        if(bssid!=null && bssid.equals(scan.BSSID)){  // 当前wifi加密方式
//                            wifiType = CmsUtil.getWifiType(scan.capabilities);
                        //if(m_iConnWifiType<0) m_iConnWifiType = wifiType;
//                        }
                }
            }
        }
        Logger.i("ApScanRunnable", "AP scan exit!");
        if(!isInterrupted) {
            onDevList();
            onWifiList();
        }
    }

    private void onDevList() {
        if(listener == null) return;
        //返回扫描结果: 有设备、没有设备
        listener.onDevList(ScanDevMode.AP, devList);
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

    private boolean checkDevOne(String strMac){
        boolean ret = true;
        if(devList==null || devList.size()==0) return ret;

        int iLen = devList.size();
        for(int i=0; i<iLen; i++){
            if(strMac.equals(devList.get(i).getBSSID())) {
                ret = false;
                break;
            }
        }

        return ret;
    }

}
