package com.iermu.client;

import android.net.wifi.ScanResult;

import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;

import java.util.List;

/**
 * 添加(安装)摄像机设备业务
 *  1.AP扫描
 *  2.upnp扫描
 *
 *   //新流程:
 *   //------------------------------
 *   //1.扫描设备
 *   //2.设备列表: 选择要连接的设备
 *   //3.获取Wifi列表
 *   //4.输入Wifi配置(密码), 开始配置设备
 *   //5.连接摄像机: 注册设备、配置设备、设备关联云服务器
 *
 * Created by wcy on 15/6/21.
 */
public interface ISetupDevBusiness extends IBaseBusiness {

    /**
     * 注册添加摄像机设备流程监听器
     * @param listener
     */
    public void addSetupDevListener(OnSetupDevListener listener);

    /**
     * 取消注册添加摄像机设备流程监听器
     * @param listener
     */
    public void removeSetupDevListener(OnSetupDevListener listener);

    /**
     * 扫描摄像机设备 (同时开启Ap、Upnp两种模式)
     */
    public void scanCam(CamDevConf camDevConf);

    /**
     * 扫描指定的摄像机设备(重新配置设备)
     * @param camDevID
     */
    public void scanSpecifiedCam(String camDevID);

    /**
     * 扫描手机Wifi热点
     */
    public void scanWifi();

    /**
     * 检测摄像机配置环境
     * @param camDev        扫描到的设备
     * @param manualMode    手动模式连接摄像机AP
     * @param conf          (该字段不能为null)
     *                      Wifi SSID、Wifi Account、Wifi 密码、固定IP、子网掩码、网关
     */
    public void checkCamEnvironment(CamDev camDev, boolean manualMode, CamDevConf conf);

    /**
     * 连接摄像机设备(指定的摄像机、配置摄像机设备)
     * @param camDev        连接的设备
     * @param manualMode    手动模式连接摄像机AP
     * @param conf          (该字段不能为null)
     *                      Wifi SSID、Wifi Account、Wifi 密码、固定IP、子网掩码、网关
     */
    public void connectCam(CamDev camDev, boolean manualMode, CamDevConf conf);

    /**
     * 手动注册摄像机(指定的摄像机)
     * @param camDev
     */
    public void registerCamStep(CamDev camDev);

    /**
     * 手动注册摄像机(指定的摄像机)
     *
     * @param camDev
     */
    @Deprecated
    public void _registerCam(CamDev camDev);

    /**
     * 退出安装设备流程
     */
    public void quitSetupDev();

    /**
     * 退出扫描设备
     */
    public void quitScanCam();

    /**
     * 退出Wifi扫描流程
     */
    public void quitScanWifi();

    /**
     * 扫描连接Wifi SSID
     * @return
     */
    public String scanConnectSSID();

    /**
     * 扫描连接Wifi BSSID
     * @return
     */
    public String scanConnectBSSID();

    /**
     * 扫描连接WifiType
     * @return
     */
    public int getConnectWifiType();

    /**
     * 获取搜索到的摄像机设备
     * @return
     */
    public List<CamDev> getScanCamDev();

    /**
     * 是否已经找到Smart设备
     * @return
     */
    public boolean existedSmartCamDev();

    /**
     * 获取扫描Wifi结果
     * @return
     */
    public List<ScanResult> getScanWifi();

}
