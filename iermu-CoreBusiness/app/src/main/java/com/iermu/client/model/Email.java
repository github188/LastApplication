package com.iermu.client.model;

import java.io.Serializable;

/**
 *  邮件配置信息模型
 *
 * Created by wcy on 15/7/1.
 */
public class Email implements Serializable {

    private String from;        //发件人
    private String to;          //收件人
    private String cc;          //抄送
    private String server;      //服务器
    private String user;        //用户名
    private String passwd;      //密码
    private String port;        //端口

    private boolean isSSL;      //是否开启SSL

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setIsSSL(boolean isSSL) {
        this.isSSL = isSSL;
    }
}
