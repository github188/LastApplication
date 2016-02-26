package com.iermu.client.test;

import android.net.wifi.ScanResult;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.client.business.impl.setupdev.SetupDevBusImpl;
import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.model.CamDev;
import com.cms.iermu.cms.WifiAdmin;
import com.iermu.client.model.CamDevConf;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加(安装)摄像机设备业务(测试类)
 *
 * Created by wcy on 15/6/22.
 */
public class TestSetupDevImpl extends SetupDevBusImpl implements ISetupDevBusiness {

    @Override
    public void addSetupDevListener(OnSetupDevListener listener) {
        super.addSetupDevListener(listener);
    }

    @Override
    public void scanCam(CamDevConf camDevConf) {
        //添加测试数据
        List<CamDev> list = new ArrayList<CamDev>();
        CamDev camDev = new CamDev();
        camDev.setBSSID("00:20:1b:10:38:2b");
        camDev.setSSID("iermu3001259");
        camDev.setDevType(0);
        list.add(camDev);

        CamDev camDev1 = new CamDev();
        camDev1.setBSSID("00:20:1b:19:fa:cb");
        camDev1.setSSID("iermu3640907");
        camDev.setDevType(0);
        list.add(camDev1);
        //appendCamDevList(list);
        onScanCamList();
    }

    @Override
    public void scanWifi() {
        WifiAdmin wifiAdmin = new WifiAdmin(ErmuApplication.getContext());
        wifiAdmin.startScan();
        // 将手机当前连接wifi在列表中第一个显示
        // 扫描结果列表
        List<ScanResult> list = wifiAdmin.getWifiList();
        appendWifiList(list);
        onScanWifiList();
    }

    @Override
    public void connectCam(CamDev dev, boolean connAp, CamDevConf conf) {
        super.connectCam(dev, connAp, conf);
    }

    @Override
    public void quitSetupDev() {

    }
}
