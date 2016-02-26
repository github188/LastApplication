package com.iermu.client.model;

import java.io.Serializable;

/**
 * Created by zhangxq on 15/9/7.
 */
public class AlarmDeviceItem implements Serializable {
    private String deviceId;
    private String deviceName;
    private String thumbImageUrl;
    private boolean alarmIsOpen;
    private long count;
    private AlarmImageData imageData;
    private boolean hasNew;

    public AlarmDeviceItem() {

    }

    public AlarmDeviceItem(String deviceId, String deviceName,
                           String thumbImageUrl, boolean alarmIsOpen,
                           long count, AlarmImageData imageData) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.thumbImageUrl = thumbImageUrl;
        this.alarmIsOpen = alarmIsOpen;
        this.count = count;
        this.imageData = imageData;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getThumbImageUrl() {
        return thumbImageUrl;
    }

    public void setThumbImageUrl(String thumbImageUrl) {
        this.thumbImageUrl = thumbImageUrl;
    }

    public boolean isAlarmIsOpen() {
        return alarmIsOpen;
    }

    public void setAlarmIsOpen(boolean alarmIsOpen) {
        this.alarmIsOpen = alarmIsOpen;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public AlarmImageData getImageData() {
        return imageData;
    }

    public void setImageData(AlarmImageData imageData) {
        this.imageData = imageData;
    }

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }
}
