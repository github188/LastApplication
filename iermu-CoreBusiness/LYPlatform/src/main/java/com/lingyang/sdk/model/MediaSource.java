package com.lingyang.sdk.model;

/**
 * 绑定设备参数
 *
 * Created by wcy on 15/8/4.
 */
public class MediaSource {

    private String hashId;  //绑定设备hash Cid
    private int status;     //绑定设备状态参数
    private int natType;    //绑定设备nat类型
    private boolean isBound;//
    private boolean isBroad;//

    public boolean isBound() {
        return isBound;
    }

    public void setBound(boolean isBound) {
        this.isBound = isBound;
    }

    public boolean isBroad() {
        return isBroad;
    }

    public void setBroad(boolean isBroad) {
        this.isBroad = isBroad;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNatType() {
        return natType;
    }

    public void setNatType(int natType) {
        this.natType = natType;
    }
}
