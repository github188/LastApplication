package com.iermu.client.model;

/**
 *  摄像机更多设置模型(摄像机状态信息)
 *
 * Created by zhoushaopei on 15/7/1.
 */
public class CamStatus {

    private boolean power;      //开关机状态
    private boolean light;      //指示灯
    private boolean invert;     //画面是否倒置
    private boolean audio;      //音频开关
    private int     localplay;  //局域网播放
    private int     scene;      //场景
    private int     nightmode;  //夜市模式
    private int     exposemode; //曝光模式
    private int     bitlevel;   //清晰度
    private int     bitrate;    //码率
    private String  maxspeed;   //最大限速
    private String  minspeed;   //最低限速
    private Email   email;      //邮件设置

    public int getLocalplay() {
        return localplay;
    }

    public void setLocalplay(int localplay) {
        this.localplay = localplay;
    }

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    public int getNightmode() {
        return nightmode;
    }

    public void setNightmode(int nightmode) {
        this.nightmode = nightmode;
    }

    public int getExposemode() {
        return exposemode;
    }

    public void setExposemode(int exposemode) {
        this.exposemode = exposemode;
    }

    public int getBitlevel() {
        return bitlevel;
    }

    public void setBitlevel(int bitlevel) {
        this.bitlevel = bitlevel;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public String getMaxspeed() {
        return maxspeed;
    }

    public void setMaxspeed(String maxspeed) {
        this.maxspeed = maxspeed;
    }

    public String getMinspeed() {
        return minspeed;
    }

    public void setMinspeed(String minspeed) {
        this.minspeed = minspeed;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public boolean isLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public boolean isInvert() {
        return invert;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

}
