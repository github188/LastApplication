package com.iermu.client.test;

/**
 * 授权用户
 * Created by zhoushaopei on 15/7/13.
 */
public class GrandUser {

    private String  userName;   //用户名
    private String  avatar;     //头像
    private int     time;       //

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
