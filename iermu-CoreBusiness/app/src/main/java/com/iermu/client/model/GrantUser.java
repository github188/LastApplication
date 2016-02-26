package com.iermu.client.model;

/**
 * 授权家人列表
 * Created by zhoushaopei on 15/8/13.
 */
public class GrantUser {

    private String uk;
    private String name;
    private String authCode;
    private String time;

    public String getUk() {
        return uk;
    }

    public void setUk(String uk) {
        this.uk = uk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
