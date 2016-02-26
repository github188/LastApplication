package com.iermu.client.model;

/**
 * Created by zhangxq on 15/9/11.
 */
public class CamSettingData {
    private long id;
    private String uniqueId;
    private String uid;
    private String deviceId;
    private int isAlarmOpen;
    private String infoJson;


    public CamSettingData() {

    }

    public CamSettingData(Long id, String uniqueId, String uid, String deviceId, int isAlarmOpen, String infoJson) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.uid = uid;
        this.deviceId = deviceId;
        this.isAlarmOpen = isAlarmOpen;
        this.infoJson = infoJson;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setIsAlarmOpen(int isAlarmOpen) {
        this.isAlarmOpen = isAlarmOpen;
    }

    public Long getId() {
        return id;
    }

    public int getIsAlarmOpen() {
        return isAlarmOpen;
    }

    public String getInfoJson(){
        return infoJson;
    }

    public  void  setInfoJson(String infoJson){
        this.infoJson = infoJson;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean hasId() {
        return false;
    }

    public boolean hasUniqueId() {
        if (uniqueId != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasUid() {
        if (uid != null) {
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

    public boolean hasIsAlarmOpen() {
        return true;
    }

    public boolean hasInfoJson() {
        if (infoJson != null) {
            return true;
        } else {
            return false;
        }
    }

    public static class Builder {

        private long id;
        private String uniqueId;
        private String uid;
        private String deviceId;
        private int isAlarmOpen;
        private String infoJson;

        public void setId(long id) {
            this.id = id;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public void setIsAlarmOpen(int isAlarmOpen) {
            this.isAlarmOpen = isAlarmOpen;
        }

        public  void  setInfoJson(String infoJson){
            this.infoJson = infoJson;
        }

        public CamSettingData build() {
            CamSettingData settingData = new CamSettingData();
            settingData.setId(id);
            settingData.setUniqueId(uniqueId);
            settingData.setUid(uid);
            settingData.setDeviceId(deviceId);
            settingData.setIsAlarmOpen(isAlarmOpen);
            settingData.setInfoJson(infoJson);
            return settingData;
        }
    }
}
