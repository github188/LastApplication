package com.iermu.client.business.impl.setupdev;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.cms.iermu.cms.CmsUtil;
import com.cms.iermu.cms.WifiAdmin;
import com.cms.iermu.cms.cmsNative;
import com.cms.iermu.cms.upnp.UpnpUtil;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.constant.CamDevType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.ScanDevMode;
import com.iermu.client.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Upno模式扫描摄像机设备
 *
 * Created by wcy on 15/6/22.
 */
public class UpnpScanRunnable extends Thread {

    private String baiduUID;//m_baiduUser.strUID
    private String camDevID;
    private WifiAdmin wifiAdmin;
    private List<CamDev> devList;
    private OnScanCamDevListener listener;
    private boolean interrupted = false;
    private long startTime = 0;

    public UpnpScanRunnable(String baiduUID, Context context) {
        this.baiduUID = baiduUID;
        this.devList = new ArrayList<CamDev>();
        this.wifiAdmin = new WifiAdmin(context);
    }

    /**
     * 查找指定设备的ID
     * @param camDevID
     */
    public UpnpScanRunnable setFindDevID(String camDevID) {
        this.camDevID = camDevID;
        return this;
    }

    public UpnpScanRunnable setListener(OnScanCamDevListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.interrupted = true;
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        while (!interrupted && System.currentTimeMillis()-startTime<=10000) {
            //扫描Upnp设备
            String ethDev = UpnpUtil.sendSSDPSearchMessage(null, 15);// 有线设备
            if(ethDev == null) {
                continue;
            }
            Logger.i("UpnpScanRunnable", "ethDev:" + ethDev);
            String[] strDevs = CmsUtil.split(ethDev, "\n");
            for(int i=0; i<strDevs.length; i++){
                String[] strDev = CmsUtil.split(strDevs[i], "/");
                if(strDev == null || strDev.length<3) {
                    continue;
                }

                // 需要屏蔽正常状态的摄像头，只检测处于配置状态的摄像头
                if(strDev.length<6 || strDev[3].indexOf("mode=")<0) continue;
                boolean bEnthernet = strDev[3].substring(5).equals("0");
                boolean bAudioSet = false;
                if(strDev.length>6 && strDev[6].indexOf("audiocfg=")>=0){
                    bAudioSet = strDev[6].substring(9).equals("1"); // 通过音频配置切wifi
                }
                int connectType = ConnectType.BAIDU;
                if(strDev.length>7 && strDev[7].indexOf("cloud=")>=0) {//cloud: 0:百度 1:羚羊 50:自有云
                    int cloud = Integer.valueOf(strDev[7].substring(6));
                    Logger.i("UpnpScanRunnable", "cloud:" + cloud);
                    if(cloud == 0) {
                        connectType = ConnectType.BAIDU;
                    } else if(cloud == 1) {
                        connectType = ConnectType.LINYANG;
                    } else if(cloud == 50) {
                        connectType = ConnectType.OTHER;
                    }
                }

                boolean bAuthFailDev = !strDev[5].substring(4).equals("0");

                if(!bEnthernet && !bAuthFailDev && !bAudioSet) continue; // 0-配置状态 1-工作状态 2-切换状态

                CamDev camdev = new CamDev();
                camdev.setDevType(CamDevType.TYPE_ETH);
                camdev.setDevIP(strDev[0]);
                camdev.setDevID(strDev[1]);
                camdev.setDevPwd((bEnthernet || bAudioSet)? cmsNative.getIpcLogPwd("3") : baiduUID);//strDev[2]);
                camdev.setWifiType(wifiAdmin.getSecurity());
                camdev.setConnectType(connectType);

                //devrow.password = mydev.devPwd;
                String devID = camdev.getDevID();
                boolean b = checkDevOneByID(devID);
                if(b && !TextUtils.isEmpty(camDevID) && camDevID.equals(devID)) {
                    devList.add(0, camdev);
                    continue;
                } else if(b){
                    Logger.i("UpnpScanRunnable", "devList devId:" + camdev.getDevID());
                    devList.add(camdev);
                }
            }
            if(listener != null) {
                //返回扫描结果: 有设备、没有设备
                listener.onDevList(ScanDevMode.UPNP, devList);
            }
            SystemClock.sleep(300);
        }
        Logger.i("UpnpScanRunnable", "UPNP scan exit!");
    }

    private boolean checkDevOneByID(String strDevID){
        boolean ret = true;
        if(devList==null || devList.size()==0) return ret;

        int iLen = devList.size();
        for(int i=0; i<iLen; i++){
            if(strDevID.equals(devList.get(i).getDevID())) {
                ret = false;
                break;
            }
        }

        return ret;
    }


}
