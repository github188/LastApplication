package com.iermu.client.model;

import com.iermu.lan.model.CamRecord;

/**
 * 录像流媒体模型
 *
 * Created by wcy on 15/9/16.
 */
public class RecordMedia {

    private int     connectType;//ConnectType 平台类型
    private String  deviceId;   //设备ID
    private int     status;     //直播状态 (百度) ｜ 摄像机连接状态(羚羊云)
    private String  playUrl;    //直播地址 百度(真实的地址)｜羚羊(不是真实的)
    private String  hashId;     //羚羊云平台设备HashId
    private int     connectRet;    //羚羊云ConnectMediaSource返回值
    private CamRecord camRecord;

    public int getConnectRet() {
        return connectRet;
    }

    public void setConnectRet(int connectRet) {
        this.connectRet = connectRet;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public void setCamRecord(CamRecord camRecord) {
        this.camRecord = camRecord;
    }

    public CamRecord getCamRecord() {
        return camRecord;
    }
}
