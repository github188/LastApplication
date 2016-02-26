package com.iermu.client.listener;

import com.iermu.client.business.impl.setupdev.setup.SetupStatus;
import com.iermu.client.model.CamDev;

import java.util.List;

/**
 * 添加(安装)摄像机设备事件
 *
 * Created by wcy on 15/6/22.
 */
public abstract class OnSetupDevListener {

    /**
     * 扫描摄像机结果列表
     * @param list
     */
    public void onScanCamList(List<CamDev> list) {}

    /**
     * 扫描Wifi结果列表
     */
    public void onScanWifiList() {}

    /**
     * 扫描失败
     * @param businessCode
     * @param message
     */
    public void onScanFail(int businessCode, String message) {}

//    public void onConfigureDev(int businessCode, String message);
//    设备注册云服务成功
//    设备已注册
//
//    /**
//     * 注册设备成功
//     * @param success
//     */
//    @Deprecated
//    public void onRegisterDev(boolean success){};
//
//    public void onConnectDev();

    /**
     * 安装(配置)设备进度状态
     * @param status
     */
    public void onSetupStatus(SetupStatus status){};

//    /**
//     * 安装摄像头成功
//     * @param success
//     */
//    @Deprecated
//    public void onSetupDev(boolean success) {}

    /**
     * 连接摄像头进度
     * @param progress
     */
    public void onUpdateProgress(int progress){};

}
