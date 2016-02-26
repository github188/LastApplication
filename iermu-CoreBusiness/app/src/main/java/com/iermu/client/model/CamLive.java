package com.iermu.client.model;

import java.io.Serializable;

/**
 * 我的摄像机
 * <p/>
 * Created by wcy on 15/6/26.
 */
public class CamLive implements Serializable,Copyable<CamLive> {

    private String  uniqueId;    //数据库使用唯一健
    private String  uid;
    private String  shareId;     //公开分享的ID
    private String  deviceId;    //摄像机设备ID
    private String  uk;          //用户ID
    private String  description; //摄像机描述
    private int     shareType;   //分享类型       (0:未分享 1:公开分享不带录像 2:私密分享不带录像 3:公开分享带录像 4:私密分享带录像)
    private int     status;      //摄像机状态     ()
    private String  thumbnail;   //摄像机缩略图
    private int     dataType;    //我的摄像机类型 (0:自己的 1:授权 2:订阅(收藏) )
    private int     connectType; //1: 百度
    private String  connectCid;  //羚羊云Cid
    private String  streamId;    //流媒体上传流的ID
    private String  cvrDay;      //录像服务时间
    private long    cvrEndTime;  //录像结束时间

    private String  avator;      //头像
    private String  ownerName;   //摄像机用户名
    private int     personNum;   //观看人数
    private String  goodNum;     //点赞个数
    private int     storeStatus; //收藏状态 （0：未收藏，1：已收藏）
    private int     grantNum;    //授权用户数
    private int     needupdate;    //是否需要升级（0：不需要升级，1：需要升级）
    private int     forceUpgrade;    //是否强制升级（0：不强制升级，1：强制升级）

    public CamLive(){}

    public CamLive(String shareId, String deviceId, String uk,
                   String description, int shareType, int status,
                   String thumbnail, int dataType, int connectType,
                   String connectDid, String streamId, String cvrDay,
                   long cvrEndTime, String avator, String ownerName,
                   int personNum, String goodNum, int storeStatus,
                   String uniqueId, String uid, int needupdate, int forceUpgrade) {
        this.uniqueId = uniqueId;
        this.uid = uid;
        this.shareId = shareId;
        this.deviceId = deviceId;
        this.uk = uk;
        this.description = description;
        this.shareType = shareType;
        this.status = status;
        this.thumbnail = thumbnail;
        this.dataType = dataType;
        this.connectType = connectType;
        this.connectCid = connectCid;
        this.streamId = streamId;
        this.cvrDay = cvrDay;
        this.cvrEndTime = cvrEndTime;
        this.avator = avator;
        this.ownerName = ownerName;
        this.personNum = personNum;
        this.goodNum = goodNum;
        this.storeStatus = storeStatus;
        this.needupdate = needupdate;
        this.forceUpgrade = forceUpgrade;
    }

    @Override
    public void fromCopy(CamLive source) {
        if(source == null) throw new NullPointerException("Source not be null.");

        this.shareId     = source.getShareId();
        this.deviceId    = source.getDeviceId();
        this.uk          = source.getUk();
        this.description = source.getDescription();
        this.shareType   = source.getShareType();
        this.status      = source.getStatus();
        this.thumbnail   = source.getThumbnail();
        this.dataType    = source.getDataType();
        this.connectType = source.getConnectType();
        this.connectCid  = source.getConnectCid();
        this.streamId    = source.getStreamId();
        this.cvrDay      = source.getCvrDay();
        this.cvrEndTime  = source.getCvrEndTime();
        this.avator      = source.getAvator();
        this.ownerName   = source.getOwnerName();
        this.personNum   = source.getPersonNum();
        this.goodNum     = source.getGoodNum();
        this.storeStatus = source.getStoreStatus();
        this.grantNum    = source.getGrantNum();
        this.needupdate  = source.getNeedupdate();
        this.forceUpgrade  = source.getForceUpgrade();
    }

    /**
     * 摄像机是否离线
     * @return
     */
    public boolean isOffline() {
        //if(connectType == ConnectType.BAIDU) {
        //    return status==0;
        //} else if(connectType == ConnectType.LINYANG) {
        //    return status<=0 || status == 255 || status == 254;
        //}
        return status==0;
    }

    /**
     * 摄像机是否关机
     * @return
     */
    public boolean isPowerOff() {
        //if(connectType == ConnectType.BAIDU) {
        //    return status> 0 && ((status&4) == 0);
        //} else if(connectType == ConnectType.LINYANG) {
        //    return status<1;
        //}
        return status> 0 && ((status&4) == 0);
    }

    /**
     * 摄像机是否开机
     * @return
     */
    public boolean isPowerOn() {
        //if(connectType == ConnectType.BAIDU) {
        //    return status> 0 && ((status&4) == 4);
        //} else if(connectType == ConnectType.LINYANG) {
        //    return status>=1;
        //}
        return status> 0 && ((status&4) == 4);
    }

    public int getGrantNum() {
        return grantNum;
    }

    public void setGrantNum(int grantnum) {
        this.grantNum = grantnum;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUk() {
        return uk;
    }

    public void setUk(String uk) {
        this.uk = uk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public String getConnectCid() {
        return connectCid;
    }

    public void setConnectCid(String connectCid) {
        this.connectCid = connectCid;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getCvrDay() {
        return cvrDay;
    }

    public void setCvrDay(String cvrDay) {
        this.cvrDay = cvrDay;
    }

    public long getCvrEndTime() {
        return cvrEndTime;
    }

    public void setCvrEndTime(long cvrEndTime) {
        this.cvrEndTime = cvrEndTime;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public String getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(String goodNum) {
        this.goodNum = goodNum;
    }

    public int getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(int storeStatus) {
        this.storeStatus = storeStatus;
    }

    public boolean hasUniqueId() {
        return uniqueId != null;
    }

    public boolean hasDeviceId() {
        return deviceId != null;
    }

    public boolean hasShareId() {
        return shareId != null;
    }

    public boolean hasUk() {
        return uk != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasShareType() {
        return true;
    }

    public boolean hasStatus() {
        return true;
    }

    public boolean hasThumbnail() {
        return thumbnail != null;
    }

    public boolean hasDataType() {
        return true;
    }

    public boolean hasConnectType() {
        return true;
    }

    public boolean hasConnectCid() {
        return connectCid != null;
    }

    public boolean hasStreamId() {
        return streamId != null;
    }

    public boolean hasCvrDay() {
        return cvrDay != null;
    }

    public boolean hasCvrEndTime() {
        return true;
    }

    public boolean hasAvator() {
        return avator != null;
    }

    public boolean hasOwnerName() {
        return ownerName != null;
    }

    public boolean hasPersonNum() {
        return true;
    }

    public boolean hasGoodNum() {
        return goodNum != null;
    }

    public boolean hasStoreStatus() {
        return true;
    }

    public static Builder newBuilder() {
        return new Builder();
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

    public boolean hasUid() {
        return uid != null;
    }

    public boolean hasGrantNum() {
        return true;
    }

    public int getNeedupdate() {
        return needupdate;
    }

    public void setNeedupdate(int needupdate) {
        this.needupdate = needupdate;
    }

    public int getForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(int forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public static class Builder {
        private String  uniqueId;
        private String  uid;
        private String  shareId;
        private String  deviceId;
        private String  uk;
        private String  description;
        private int     shareType;
        private int     status;
        private String  thumbnail;
        private int     dataType;
        private int     connectType;
        private String  connectCid;
        private String  streamId;
        private String  cvrDay;
        private long    cvrEndTime;
        private String  avator;
        private String  ownerName;
        private int     personNum;
        private String  goodNum;
        private int     storeStatus;
        private int     grantNum;
        private int     needupdate;
        private int     forceUpgrade;

        public String getShareId() {
            return shareId;
        }

        public void setShareId(String shareId) {
            this.shareId = shareId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getUk() {
            return uk;
        }

        public void setUk(String uk) {
            this.uk = uk;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getShareType() {
            return shareType;
        }

        public void setShareType(int shareType) {
            this.shareType = shareType;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public int getDataType() {
            return dataType;
        }

        public void setDataType(int dataType) {
            this.dataType = dataType;
        }

        public int getConnectType() {
            return connectType;
        }

        public void setConnectType(int connectType) {
            this.connectType = connectType;
        }

        public String getConnectCid() {
            return connectCid;
        }

        public void setConnectCid(String connectCid) {
            this.connectCid = connectCid;
        }

        public String getStreamId() {
            return streamId;
        }

        public void setStreamId(String streamId) {
            this.streamId = streamId;
        }

        public String getCvrDay() {
            return cvrDay;
        }

        public void setCvrDay(String cvrDay) {
            this.cvrDay = cvrDay;
        }

        public long getCvrEndTime() {
            return cvrEndTime;
        }

        public void setCvrEndTime(long cvrEndTime) {
            this.cvrEndTime = cvrEndTime;
        }

        public String getAvator() {
            return avator;
        }

        public void setAvator(String avator) {
            this.avator = avator;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public int getPersonNum() {
            return personNum;
        }

        public void setPersonNum(int personNum) {
            this.personNum = personNum;
        }

        public String getGoodNum() {
            return goodNum;
        }

        public void setGoodNum(String goodNum) {
            this.goodNum = goodNum;
        }

        public int getStoreStatus() {
            return storeStatus;
        }

        public void setStoreStatus(int storeStatus) {
            this.storeStatus = storeStatus;
        }

        public void setGrantNum(int grantNum) {
            this.grantNum = grantNum;
        }

        public int getGrantNum() {
            return grantNum;
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

        public int getNeedupdate() {
            return needupdate;
        }

        public void setNeedupdate(int needupdate) {
            this.needupdate = needupdate;
        }

        public int getForceUpgrade() {
            return forceUpgrade;
        }

        public void setForceUpgrade(int forceUpgrade) {
            this.forceUpgrade = forceUpgrade;
        }

        public CamLive build() {
            CamLive camLive = new CamLive();
            camLive.setUniqueId(uniqueId);
            camLive.setUid(uid);
            camLive.setDeviceId(deviceId);
            camLive.setShareId(shareId);
            camLive.setUk(uk);
            camLive.setDescription(description);
            camLive.setShareType(shareType);
            camLive.setStatus(status);
            camLive.setThumbnail(thumbnail);
            camLive.setDataType(dataType);
            camLive.setConnectType(connectType);
            camLive.setConnectCid(connectCid);
            camLive.setStreamId(streamId);
            camLive.setCvrDay(cvrDay);
            camLive.setCvrEndTime(cvrEndTime);
            camLive.setAvator(avator);
            camLive.setOwnerName(ownerName);
            camLive.setPersonNum(personNum);
            camLive.setGoodNum(goodNum);
            camLive.setStoreStatus(storeStatus);
            camLive.setGrantNum(grantNum);
            camLive.setNeedupdate(needupdate);
            camLive.setForceUpgrade(forceUpgrade);
            return camLive;
        }
    }
}
