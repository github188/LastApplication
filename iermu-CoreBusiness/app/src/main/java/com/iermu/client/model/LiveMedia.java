package com.iermu.client.model;

import android.net.Uri;
import android.text.TextUtils;

import com.iermu.client.model.constant.ConnectType;

/**
 * 直播信息(基类 百度、羚羊)
 *
 * 百度状态:
 *  -1 摄像机连接失败 (客户端自定义)
 *
 * 羚羊云状态:
 *  -1 摄像机连接失败 (客户端自定义)
 *  0 离线
 *  1 准备就绪
 *  2 未就绪
 *  3 直播中
 *
 * Created by wcy on 15/8/5.
 */
public class LiveMedia {

    private int     connectType;//ConnectType 平台类型
    private String  deviceId;   //设备ID
    private int     status;     //羚羊云:摄像机连接状态 百度:直播状态
    private String  playUrl;    //直播地址
    private String  type;       //流媒体类型(P2P、RTMP)
    private String  description;//摄像机名称

    private String  devToken;   //羚羊云:DevToken
    private long    expiresIn;  //羚羊云DevToken的有效时长(秒数)
    private int     lyStatus;   //羚羊云直播状态码
    private String  trackIp;    //羚羊云
    private int     trackPort;  //羚羊云
    private long    startTime;  //羚羊云:取到开始时间(秒数)

    private int     playNum;    //百度:当前播放数
    private int     maxPlayNum; //百度:最大播放数
    private int     userPlayNum;//百度:

    private int     connectRet;    //羚羊云ConnectMediaSource返回值
    private boolean isLanRtmp;  //是否是局域网

    public LiveMedia() {
        startTime = System.currentTimeMillis();
    }

    public boolean isInvalidToken() {
        long l = System.currentTimeMillis() - startTime;
        return (l/1000 + 60*1000) >= expiresIn;
    }

    /**
     * 获取百度云流媒体直播地址 有效时间(秒数)
     * @return
     */
    public long getEffectiveTime() {
        long time = 0;
        if( !TextUtils.isEmpty(playUrl) ) {
            Uri uri = Uri.parse(playUrl);
            String time1 = uri.getQueryParameter("time");
            String expire1 = uri.getQueryParameter("expire");
            if(TextUtils.isEmpty(time1) || TextUtils.isEmpty(expire1)) {
                return 0;
            }
            long createTime = Long.parseLong(time1);
            long expire = Long.parseLong(expire1);
            return (expire-createTime)*1000L;
        }
        return time;
    }

    /**
     * 打开直播失败|摄像机连接失败
     * @return
     */
    public boolean isOffLive() {
        return status==-1;
    }

    /**
     * 摄像机是否离线
     * @return
     */
    public boolean isOffline() {
        return status==0;
    }

    /**
     * 摄像机是否关机 (百度)
     * @return
     */
    public boolean isPowerOff() {
        return status> 0 && ((status&4) == 0);
    }

    /**
     * 摄像机是否开机 (百度)
     * @return
     */
    public boolean isPowerOn() {
        return status> 0 && ((status&4) == 4);
    }

    /**
     * 摄像机是否可以直播
     * @return
     */
    public boolean isLiveOn() {
        if (connectType == ConnectType.BAIDU) {
            return status > 0 && ((status & 4) == 4);//百度
        }
        return lyStatus == 1; //羚羊云摄像机状态: 1准备就绪
    }

    /**
     * 连接摄像机是否成功(羚羊云)
     * @return
     */
    public boolean isConnected() {
        return connectRet==0;
    }

    /**
     * 根据摄像头状态检测是否有云台
     *
     * 在APP端收到的属性中，
     * 8~15位为自定义属性字节，其含义如下：
     * 8位表示有无云台，1有；
     * 9~12位转成整数，表示产品类型（如0：通用产品；3：红板凳）；
     * 13~15位保留为0；
     * @return  暂时去掉检测机制
     */
    public boolean hasCloudPlatform() {
        return ((status>>8)&1)==1;
    }

    /**
     * 判断是否是Rtmp地址
     * @return
     */
    public boolean isRtmpLive() {
        return playUrl.startsWith("rtmp://");
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public int getLyStatus() {
        return lyStatus;
    }

    public void setLyStatus(int lyStatus) {
        this.lyStatus = lyStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevToken() {
        return devToken;
    }

    public void setDevToken(String devToken) {
        this.devToken = devToken;
    }

    public String getTrackIp() {
        return trackIp;
    }

    public void setTrackIp(String tractIp) {
        this.trackIp = tractIp;
    }

    public int getTrackPort() {
        return trackPort;
    }

    public void setTrackPort(int tractPort) {
        this.trackPort = tractPort;
    }

    public boolean isLanRtmp() {
        return isLanRtmp;
    }

    public void setLanRtmp(boolean isLanRtmp) {
        this.isLanRtmp = isLanRtmp;
    }

    public int getConnectRet() {
        return connectRet;
    }

    public void setConnectRet(int connectRet) {
        this.connectRet = connectRet;
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

    public int getPlayNum() {
        return playNum;
    }

    public void setPlayNum(int playNum) {
        this.playNum = playNum;
    }

    public int getMaxPlayNum() {
        return maxPlayNum;
    }

    public void setMaxPlayNum(int maxPlayNum) {
        this.maxPlayNum = maxPlayNum;
    }

    public int getUserPlayNum() {
        return userPlayNum;
    }

    public void setUserPlayNum(int userPlayNum) {
        this.userPlayNum = userPlayNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}
