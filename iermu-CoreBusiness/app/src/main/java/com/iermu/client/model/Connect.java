package com.iermu.client.model;

/**
 * 平台信息(百度云、羚羊云)
 *
 * Created by wcy on 15/7/31.
 */
public class Connect {

    private int    connectType; //平台类型
    private String uid;         //平台UID
    private String userName;   //百度账号
    private String secret;      //平台密钥 (羚羊云使用)
    private int    status;      //平台状态
    private String cid;         //CID (羚羊云使用)
    private String userToken;   //UserToken(羚羊云使用)
    private String userConfig;  //UserConfig(羚羊云使用)
    private String accessToken; //百度accessToken；
    private String refreshToken;//百度refreshToken

    public String getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(String userConfig) {
        this.userConfig = userConfig;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
