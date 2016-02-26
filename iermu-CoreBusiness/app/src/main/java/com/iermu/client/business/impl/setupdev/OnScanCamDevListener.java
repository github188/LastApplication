package com.iermu.client.business.impl.setupdev;

import android.net.wifi.ScanResult;

import com.iermu.client.model.CamDev;
import com.iermu.client.model.constant.ScanDevMode;

import java.util.List;

/**
 * 扫描摄像机设备线程监听器
 *
 * Created by wcy on 15/6/22.
 */
public abstract class OnScanCamDevListener {

    /**
     * 扫描摄像机设备列表
     * @param mode
     * @param list
     */
    public void onDevList(ScanDevMode mode, List<CamDev> list){};

    /**
     * 扫描Wifi列表
     * @param list
     */
    public void onWifiList(List<ScanResult> list){};

    /**
     * Wifi关闭
     */
    public void onWifiClose(){};

}
