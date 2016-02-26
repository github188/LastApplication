package com.iermu.client.model;

import android.net.wifi.ScanResult;

import java.io.Serializable;

/**
 * 摄像机设备配置参数
 *
 * Created by wcy on 15/6/22.
 */
public class CamDevConf implements Serializable {

    private ScanResult  scanWifi;   //配置连接的Wifi
    private String      wifiSSID;   //配置Wifi SSID
    private String      wifiAccount;//配置Wifi Account
    private String      wifiPwd;    //配置Wifi 密码
    private int         wifiType;   //配置Wifi 类型
    private String      dhcpIP;     //固定IP地址
    private String      dhcpNetmask;//子网掩码
    private String      dhcpGateway;//网关

    public CamDevConf(String wifiSSID) {
        this.wifiSSID = wifiSSID;
    }

//    public CamDevConf(ScanResult scanWifi, String wifiAccount, String wifiPwd, String dhcpIP, String dhcpNetmask, String dhcpGateway) {
//        this.scanWifi = scanWifi;
//        this.wifiAccount = wifiAccount;
//        this.wifiPwd = wifiPwd;
//        this.dhcpIP = dhcpIP;
//        this.dhcpNetmask = dhcpNetmask;
//        this.dhcpGateway = dhcpGateway;
//    }

    public String getSSID() {
        return (scanWifi!=null) ? scanWifi.SSID : "";
    }

    public String getBSSID() {
        return (scanWifi!=null) ? scanWifi.BSSID : "";
    }

    public String getCapabilities() {
        return (scanWifi!=null) ? scanWifi.capabilities : "";
    }

    public String getWifiSSID() {
        return wifiSSID;
    }

    public void setWifiSSID(String wifiSSID) {
        this.wifiSSID = wifiSSID;
    }

    public ScanResult getScanWifi() {
        return scanWifi;
    }

    public void setScanWifi(ScanResult scanWifi) {
        this.scanWifi = scanWifi;
    }

    public String getWifiAccount() {
        return wifiAccount;
    }

    public void setWifiAccount(String wifiAccount) {
        this.wifiAccount = wifiAccount;
    }

    public String getWifiPwd() {
        return wifiPwd;
    }

    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }

    public String getDhcpIP() {
        return dhcpIP;
    }

    public void setDhcpIP(String dhcpIP) {
        this.dhcpIP = dhcpIP;
    }

    public String getDhcpNetmask() {
        return dhcpNetmask;
    }

    public void setDhcpNetmask(String dhcpNetmask) {
        this.dhcpNetmask = dhcpNetmask;
    }

    public String getDhcpGateway() {
        return dhcpGateway;
    }

    public void setDhcpGateway(String dhcpGateway) {
        this.dhcpGateway = dhcpGateway;
    }

    public void setWifiType(int wifiType) {
        this.wifiType = wifiType;
    }

    public int getWifiType() {
        return wifiType;
    }
}
