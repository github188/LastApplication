package com.iermu.lan.model;

/**
 * 摄像机录像模型 (百度设备 | 羚羊云设备)
 *
 *
 * Created by zhangxq on 15/8/13.
 */
public class CamRecord {

    private int startTime;
    private int endTime;
    private String diskInfo;    //仅在羚羊云设备使用

    // 卡录参数，暂时没用到
    private String timeLen;   //录像时间长度  // for cms rec
    private String fileLen;   //录像大小

    public CamRecord() {

    }

    public CamRecord(int startTime, int endTime, String diskInfo, String timeLen, String fileLen) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.diskInfo = diskInfo;
        this.timeLen = timeLen;
        this.fileLen = fileLen;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getDiskInfo() {
        return diskInfo;
    }

    public void setDiskInfo(String diskInfo) {
        this.diskInfo = diskInfo;
    }

    public String getTimeLen() {
        return timeLen;
    }

    public void setTimeLen(String timeLen) {
        this.timeLen = timeLen;
    }

    public String getFileLen() {
        return fileLen;
    }

    public void setFileLen(String fileLen) {
        this.fileLen = fileLen;
    }
}
