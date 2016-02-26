package com.iermu.client.model;

import java.io.Serializable;

/**
 * Created by zhangxq on 15/8/21.
 */
public class AlarmImageData implements Serializable {
    private long id;
    private String title;
    private String description;
    private String deviceId;
    private String recdatetime;
    private String alarmtime;

    private String imageUrl;

    public AlarmImageData() {

    }

    public AlarmImageData(String title, String description, String deviceId, String recdatetime, String alarmtime) {
        this.title = title;
        this.description = description;
        this.deviceId = deviceId;
        this.recdatetime = recdatetime;
        this.alarmtime = alarmtime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRecdatetime() {
        return recdatetime;
    }

    public void setRecdatetime(String recdatetime) {
        this.recdatetime = recdatetime;
    }

    public String getAlarmtime() {
        return alarmtime;
    }

    public void setAlarmtime(String alarmtime) {
        this.alarmtime = alarmtime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


//////////////////////////////////////////////////////////////////////////////////////////
    public boolean hasId() {
        return false;
    }

    public boolean hasTitle() {
        if (title != null) {
            return true;
        } else {
            return false;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean hasDescription() {
        if (description != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasDeviceId() {
        if (deviceId != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasRecdatetime() {
        if (recdatetime != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasAlarmtime() {
        if (alarmtime != null) {
            return true;
        } else {
            return false;
        }
    }

    public static class Builder{

        private long id;
        private String title;
        private String description;
        private String deviceId;
        private String recdatetime;
        private String alarmtime;

        public void setId(long id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public void setRecdatetime(String recdatetime) {
            this.recdatetime = recdatetime;
        }

        public void setAlarmtime(String alarmtime) {
            this.alarmtime = alarmtime;
        }

        public AlarmImageData build() {
            AlarmImageData data = new AlarmImageData();
            data.setId(id);
            data.setTitle(title);
            data.setDescription(description);
            data.setDeviceId(deviceId);
            data.setRecdatetime(recdatetime);
            data.setAlarmtime(alarmtime);
            return data;
        }
    }
}
