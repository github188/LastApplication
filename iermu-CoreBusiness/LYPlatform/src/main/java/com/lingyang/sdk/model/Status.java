package com.lingyang.sdk.model;

import java.util.List;

/**
 *  摄像机状态
 * //    {
 //        "Status":	4,
 //            "NatType":	2,
 //            "UserName":	"zl123",
 //            "LoginSucc":	1,
 //            "CurMediaScrNo":	1,
 //            "MediaSource":	[{
 //        "HashID":	"65551100ED0F2942",
 //                "Status":	4,
 //                "NatType":	2
 //    }]
 //    }
 //网关NatType类型
 //
 //        NAT_TYPE_INTERNET = 1, 独立的公网IP
 //    NAT_TYPE_UNKOWN = 2, 未知类型
 //            NAT_TYPE_SYNMETRIC = 3, 对称类型
 //    NAT_TYPE_CONE = 4, 锥形
 //
 //    状态字段名称	字段涵义
 //    Status	用户当前状态
 //    NatType	用户nat类型
 //    UserName	用户名
 //    LoginSucc	登录成功标记（1，成功；0，失败)
 //    CurMediaScrNo	当前用户连接设备序号（MediaSource索引序号)
 //    MediaSource	绑定设备参数
 //    MediaSource － HashID	绑定设备hash Cid
 //    MediaSource － Status	绑定设备状态参数
 //    MediaSource － NatType	绑定设备nat类型
 //    20.5 状态的变化时序及操作规范：
 //            1.用户调用打开云平台接口后查询LoginSucc字段，1表示成功；0失败，需要排查原因。登录成功后初试状态Status为1，因为还没有连接设备CurMediaScrNo为0。UserName为登录的用户名。MediaSource会列出已绑定的设备信息列表。
 //            2.当Hash ID、Status、NatType为0时，表示设备还未在线，需等待设备上线。设备上线后，会显示这3个信息。并且当设备和客户端的Status都为1时，客户端可以发起连接请求。
 //            3.客户端发起连接请求后，播放器会处理与云平台的数据交互，用户只用开启线程监听这个状态列表即可。随着连接请求的发起，客户端和设备的Status会从1变3，建立连接。
 //            4.连接建立成功后，Status变为4，CurMediaScrNo会被置为被连接设备的序号。这时客户端就可以看到直播流了。
 //            5.当客户端调用断开连接接口后，客户端和正在直播的设备Status会变为5。
 //            6.等到连接关闭完成后，Status再被置为1。
 *
 * Created by wcy on 15/8/4.
 */
public class Status {

    private String  id;             //xx
    private int     status;         //用户当前状态
    private int     natType;        //用户nat类型
    private String  userName;       //用户名                                   //TODO 可能没有
    private boolean loginSucc;      //登录成功标记（1，成功；0，失败)
    private int     curMediaScrNo;  //当前用户连接设备序号（MediaSource索引序号)  //TODO 可能没有
    private int     bufferCount;    //
    private int     connectTime;    //
    private String  connectionInfo; //
    private String  gatewayIP;      //
    private int     lPopFrameRate;  //
    private int     lPushFrameRate; //
    private int     lPushSpeed;     //
    private int     lRecvAvgSpeed;  //
    private int     lRecvSpeed;     //
    private int     lSendAvgSpeed;  //
    private int     lSendSpeed;     //
    private int     popDelay;       //
    private int     rPopFrameRate;  //
    private int     rPushFrameRate; //
    private int     rSendAvgSpeed;  //
    private int     rSendSpeed;     //
    private long    sessionID;      //
    private long    storageExpireTime;//
    private List<MediaSource> mediaList;

    /**
     * 根据HashId查找对应的<绑定设备参数>
     * @param hashId
     * @return
     */
    public MediaSource getMediaSource(String hashId) {
        if(mediaList != null && mediaList.size() > 0) {
            for (MediaSource item : mediaList) {
                if (item.getHashId().equals(hashId))
                    return item;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNatType() {
        return natType;
    }

    public void setNatType(int natType) {
        this.natType = natType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isLoginSucc() {
        return loginSucc;
    }

    public void setLoginSucc(boolean loginSucc) {
        this.loginSucc = loginSucc;
    }

    public int getCurMediaScrNo() {
        return curMediaScrNo;
    }

    public void setCurMediaScrNo(int curMediaScrNo) {
        this.curMediaScrNo = curMediaScrNo;
    }

    public int getBufferCount() {
        return bufferCount;
    }

    public void setBufferCount(int bufferCount) {
        this.bufferCount = bufferCount;
    }

    public int getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(int connectTime) {
        this.connectTime = connectTime;
    }

    public String getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(String connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public String getGatewayIP() {
        return gatewayIP;
    }

    public void setGatewayIP(String gatewayIP) {
        this.gatewayIP = gatewayIP;
    }

    public int getlPopFrameRate() {
        return lPopFrameRate;
    }

    public void setlPopFrameRate(int lPopFrameRate) {
        this.lPopFrameRate = lPopFrameRate;
    }

    public int getlPushFrameRate() {
        return lPushFrameRate;
    }

    public void setlPushFrameRate(int lPushFrameRate) {
        this.lPushFrameRate = lPushFrameRate;
    }

    public int getlPushSpeed() {
        return lPushSpeed;
    }

    public void setlPushSpeed(int lPushSpeed) {
        this.lPushSpeed = lPushSpeed;
    }

    public int getlRecvAvgSpeed() {
        return lRecvAvgSpeed;
    }

    public void setlRecvAvgSpeed(int lRecvAvgSpeed) {
        this.lRecvAvgSpeed = lRecvAvgSpeed;
    }

    public int getlRecvSpeed() {
        return lRecvSpeed;
    }

    public void setlRecvSpeed(int lRecvSpeed) {
        this.lRecvSpeed = lRecvSpeed;
    }

    public int getlSendAvgSpeed() {
        return lSendAvgSpeed;
    }

    public void setlSendAvgSpeed(int lSendAvgSpeed) {
        this.lSendAvgSpeed = lSendAvgSpeed;
    }

    public int getlSendSpeed() {
        return lSendSpeed;
    }

    public void setlSendSpeed(int lSendSpeed) {
        this.lSendSpeed = lSendSpeed;
    }

    public int getPopDelay() {
        return popDelay;
    }

    public void setPopDelay(int popDelay) {
        this.popDelay = popDelay;
    }

    public int getrPopFrameRate() {
        return rPopFrameRate;
    }

    public void setrPopFrameRate(int rPopFrameRate) {
        this.rPopFrameRate = rPopFrameRate;
    }

    public int getrPushFrameRate() {
        return rPushFrameRate;
    }

    public void setrPushFrameRate(int rPushFrameRate) {
        this.rPushFrameRate = rPushFrameRate;
    }

    public int getrSendAvgSpeed() {
        return rSendAvgSpeed;
    }

    public void setrSendAvgSpeed(int rSendAvgSpeed) {
        this.rSendAvgSpeed = rSendAvgSpeed;
    }

    public int getrSendSpeed() {
        return rSendSpeed;
    }

    public void setrSendSpeed(int rSendSpeed) {
        this.rSendSpeed = rSendSpeed;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public long getStorageExpireTime() {
        return storageExpireTime;
    }

    public void setStorageExpireTime(long storageExpireTime) {
        this.storageExpireTime = storageExpireTime;
    }

    public List<MediaSource> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaSource> mediaList) {
        this.mediaList = mediaList;
    }
}
