package com.iermu.client.model;

import java.io.Serializable;

/**
 * 摄像机设备信息模型
 * <p/>
 * Created by zhoushaopei on 15/7/1.
 */
public class CamInfo implements Serializable {

    private String id;          //设备ID deviceId
    private String intro;       //介绍
    private String model;       //设备型号
    private String namePlate;   //摄像机型号 HDB/HDSM/HDO/HDM/HDP、其中HDM显示为：爱耳目家庭摄像机
    private String resolution;  //摄像机分辨率
    private String sn;          //序列号
    private String mac;         //设备mac地址
    private String wifi;        //wifi名称
    private String ip;          //局域网ip
    private String sig;         //wifi信号强度
    private String firmware;    //固件号
    private String firmdate;    //固件日期
    private String platform;    //设备平台号 小球=100

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmdate(String firmdate) {
        this.firmdate = firmdate;
    }

    public String getFirmdate() {
        return firmdate;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }


    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSn() {
        return sn;
    }


    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getSig() {
        return sig;
    }


    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getWifi() {
        return wifi;
    }

    public String getNamePlate() {
        return namePlate;
    }

    public void setNamePlate(String namePlate) {
        this.namePlate = namePlate;
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean hasModel() {
        return true;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String toJsonString() {
        return "{" +
                "id='" + id + '\'' +
                ", intro='" + intro + '\'' +
                ", model='" + model + '\'' +
                ", namePlate='" + namePlate + '\'' +
                ", resolution='" + resolution + '\'' +
                ", sn='" + sn + '\'' +
                ", mac='" + mac + '\'' +
                ", wifi='" + wifi + '\'' +
                ", ip='" + ip + '\'' +
                ", sig='" + sig + '\'' +
                ", firmware='" + firmware + '\'' +
                ", firmdate='" + firmdate + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }

    public static class Builder {
        private String model;

        public CamInfo build() {
            return new CamInfo();
        }

        public void setModel(String model) {
            this.model = model;
        }
    }

}
