package com.iermu.client.model;

/**
 * 预置点模型
 * <p/>
 * Created by zhangxq on 15/10/20.
 */
public class CloudPosition {
    private Long id;
    private String uniqueId;
    private String uid;
    private String deviceId;
    private int preset;
    private String title;
    private String imagePath;
    private long addDate;

    public CloudPosition() {

    }

    public CloudPosition(Long id, String uniqueId, String uid, String deviceId,
                         int preset, String title, String imagePath, long addDate) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.uid = uid;
        this.deviceId = deviceId;
        this.preset = preset;
        this.title = title;
        this.imagePath = imagePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getPreset() {
        return preset;
    }

    public void setPreset(int preset) {
        this.preset = preset;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setAddDate(long addDate) {
        this.addDate = addDate;
    }

    public long getAddDate() {
        return addDate;
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

    public boolean hasPreset() {
        return true;
    }

    public boolean hasTitle() {
        if (title != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasImagePath() {
        if (imagePath != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasAddDate() {
        return true;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String uniqueId;
        private String uid;
        private String deviceId;
        private int preset;
        private String title;
        private String imagePath;
        private long addDate;

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

        public void setId(Long id) {
            this.id = id;
        }

        public void setPreset(int preset) {
            this.preset = preset;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public void setAddDate(long addDate) {
            this.addDate = addDate;
        }

        public CloudPosition build() {
            CloudPosition cloudPosition = new CloudPosition();
            cloudPosition.setId(id);
            cloudPosition.setUniqueId(uniqueId);
            cloudPosition.setUid(uid);
            cloudPosition.setDeviceId(deviceId);
            cloudPosition.setPreset(preset);
            cloudPosition.setTitle(title);
            cloudPosition.setImagePath(imagePath);
            cloudPosition.setAddDate(addDate);
            return cloudPosition;
        }
    }
}
