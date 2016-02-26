package com.iermu.client.model;

/**
 * 云台信息
 *
 * Created by zhoushaopei on 15/10/22.
 */
public class CloudPlat {

    private boolean plat;               //云台接入状态
    private boolean platMove;           //云台位移支持
    private int platType;               //云台类型
    private boolean platRotate;         //云台平扫支持
    private boolean platRotateStatus;   //云台平扫状态
    private boolean platTrackStatus;    //云台巡航状态

    public boolean isPlat() {
        return plat;
    }

    public void setPlat(boolean plat) {
        this.plat = plat;
    }

    public boolean isPlatMove() {
        return platMove;
    }

    public void setPlatMove(boolean platMove) {
        this.platMove = platMove;
    }

    public int getPlatType() {
        return platType;
    }

    public void setPlatType(int platType) {
        this.platType = platType;
    }

    public boolean isPlatRotate() {
        return platRotate;
    }

    public void setPlatRotate(boolean platRotate) {
        this.platRotate = platRotate;
    }

    public boolean isPlatRotateStatus() {
        return platRotateStatus;
    }

    public void setPlatRotateStatus(boolean platRotateStatus) {
        this.platRotateStatus = platRotateStatus;
    }

    public boolean isPlatTrackStatus() {
        return platTrackStatus;
    }

    public void setPlatTrackStatus(boolean platTrackStatus) {
        this.platTrackStatus = platTrackStatus;
    }
}
