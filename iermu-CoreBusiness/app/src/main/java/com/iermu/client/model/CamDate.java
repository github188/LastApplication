package com.iermu.client.model;

/**
 * Created by zhangxq on 15/11/13.
 */
public class CamDate {
    int dayStartTime;
    boolean isExistRecord;

    public CamDate() {
    }

    public CamDate(int dayStartTime, boolean isExistRecord) {
        this.dayStartTime = dayStartTime;
        this.isExistRecord = isExistRecord;
    }

    public int getDayStartTime() {
        return dayStartTime;
    }

    public void setDayStartTime(int dayStartTime) {
        this.dayStartTime = dayStartTime;
    }

    public boolean isExistRecord() {
        return isExistRecord;
    }

    public void setIsExistRecord(boolean isExistRecord) {
        this.isExistRecord = isExistRecord;
    }
}
