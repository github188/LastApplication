package com.iermu.client.model.viewmodel;

import java.io.Serializable;

/**
 * Created by zsj on 15/10/28.
 */
public class WifiTransferModel implements Serializable {
    private String SSID; //wifi名称
    private String pwd; //wifi密码
    private String user; //wifi用户名
    private boolean isAdvanceCon; //手动开关
    private String ipText;//手动配置ip
    private String netmaskText;//手动配置子网掩码
    private String gatewayTtext;//手动配置网关
    private int  wifiType;//网络类型

    public WifiTransferModel(String SSID, String pwd, String user, boolean isAdvanceCon, String ipText, String netmaskText, String gatewayTtext, int wifiType) {
        this.SSID = SSID;
        this.pwd = pwd;
        this.user = user;
        this.isAdvanceCon = isAdvanceCon;
        this.ipText = ipText;
        this.netmaskText = netmaskText;
        this.gatewayTtext = gatewayTtext;
        this.wifiType = wifiType;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isAdvanceCon() {
        return isAdvanceCon;
    }

    public void setIsAdvanceCon(boolean isAdvanceCon) {
        this.isAdvanceCon = isAdvanceCon;
    }

    public String getIpText() {
        return ipText;
    }

    public void setIpText(String ipText) {
        this.ipText = ipText;
    }

    public String getNetmaskText() {
        return netmaskText;
    }

    public void setNetmaskText(String netmaskText) {
        this.netmaskText = netmaskText;
    }

    public String getGatewayTtext() {
        return gatewayTtext;
    }

    public void setGatewayTtext(String gatewayTtext) {
        this.gatewayTtext = gatewayTtext;
    }

    public int getWifiType() {
        return wifiType;
    }

    public void setWifiType(int wifiType) {
        this.wifiType = wifiType;
    }

    @Override
    public String toString() {
        return "WifiTransferModel{" +
                "SSID='" + SSID + '\'' +
                ", pwd='" + pwd + '\'' +
                ", user='" + user + '\'' +
                ", isAdvanceCon=" + isAdvanceCon +
                ", ipText='" + ipText + '\'' +
                ", netmaskText='" + netmaskText + '\'' +
                ", gatewayTtext='" + gatewayTtext + '\'' +
                ", wifiType=" + wifiType +
                '}';
    }
}
