package com.iermu.client.model.viewmodel;

import com.iermu.client.model.CronRepeat;

import java.util.Date;

/**
 *  摄像机升级状态
 *
 * Created by zsj on 15/7/1.
 */
public class CamUpdateStatus {

    private int     intStatus; //摄像机升级状态码
    private String deviceid;

    public int getIntStatus() {
        return intStatus;
    }

    public void setIntStatus(int intStatus) {
        this.intStatus = intStatus;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    @Override
    public String toString() {
        return "CamUpdateStatus{" +
                "intStatus=" + intStatus +
                ", deviceid='" + deviceid + '\'' +
                '}';
    }
}
