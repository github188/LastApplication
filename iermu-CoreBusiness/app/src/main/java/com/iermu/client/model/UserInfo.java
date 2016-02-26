package com.iermu.client.model;

import java.io.Serializable;

/**
 * 用户信息
 * Created by xjy on 15/8/27.
 */
public class UserInfo implements Serializable {

    private String uid;         //用户ID
    private String userName;    //用户名称
    private String email;       //邮箱
    private String avatar;      //用户头像
    private String mobile;      //手机
    private int emailStatus;    //邮箱状态
    private int mobileStatus;   //手机状态
    private int avatarStatus;   //头像是否上传


    public UserInfo() {
    }

    public UserInfo(String uid, String userName, String avatar) {
        this.uid = uid;
        this.userName = userName;
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(int emailStatus) {
        this.emailStatus = emailStatus;
    }

    public int getMobileStatus() {
        return mobileStatus;
    }

    public void setMobileStatus(int mobileStatus) {
        this.mobileStatus = mobileStatus;
    }

    public int getAvatarStatus() {
        return avatarStatus;
    }

    public void setAvatarStatus(int avatarStatus) {
        this.avatarStatus = avatarStatus;
    }
}
