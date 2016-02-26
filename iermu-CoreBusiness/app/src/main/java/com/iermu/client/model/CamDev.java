package com.iermu.client.model;

import java.io.Serializable;

/**
 * 摄像机设备配置数据结构
 *
 * Created by wcy on 15/6/22.
 */
public class CamDev implements Serializable {

    /**
     * @see com.iermu.client.model.constant.CamDevType
     */
    private int     devType;    //0:wifi ap 1:eth 2:wifi direct 3:smart模式
    private String  BSSID;      //设备Mac地址
    private String  SSID;       //
    private String  devIP;      //设备IP
    private String  devPwd;     //设备密码
    private String  devID;      //设备ID

    private int connectType;    //平台类型 1:百度 2:羚羊云 50:自有云
    private int wifiType;       //Ap模式扫描的设备Wifi类型
    private long scanTime;      //Smart模式:查找到设备的时间

    public long getScanTime() {
        return scanTime;
    }

    public void setScanTime(long scanTime) {
        this.scanTime = scanTime;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getDevIP() {
        return devIP;
    }

    public void setDevIP(String devIP) {
        this.devIP = devIP;
    }

    public String getDevPwd() {
        return devPwd;
    }

    public void setDevPwd(String devPwd) {
        this.devPwd = devPwd;
    }

    public String getDevID() {
        return devID;
    }

    public void setDevID(String devID) {
        this.devID = devID;
    }

    public void setWifiType(int wifiType) {
        this.wifiType = wifiType;
    }

    public int getWifiType() {
        return wifiType;
    }
}
