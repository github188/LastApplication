package com.iermu.client.model;

/**
 * 摄像机报警通知
 * Created by zhoushaopei on 15/8/3.
 */
public class CamAlarm {

    private boolean notice;   //报警通知开关
    private boolean mail;     //邮件报警开关
    private boolean audio;    //声音报警开关
    private boolean move;     //移动报警开关
    private int     audioLevel; //声音报警灵敏度
    private int     moveLevel;  //移动报警灵敏度
    private CamCron alarmCron;  //消息报警定时

    public CamCron getAlarmCron() {
        return alarmCron;
    }

    public void setAlarmCron(CamCron alarmCron) {
        this.alarmCron = alarmCron;
    }

    public boolean isNotice() {
        return notice;
    }

    public void setNotice(boolean notice) {
        this.notice = notice;
    }

    public boolean isMail() {
        return mail;
    }

    public void setMail(boolean mail) {
        this.mail = mail;
    }

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public int getAudioLevel() {
        return audioLevel;
    }
    
    public void setAudioLevel(int audioLevel) {
        this.audioLevel = audioLevel;
    }
    
    public int getMoveLevel() {
        return moveLevel;
    }
    
    public void setMoveLevel(int moveLevel) {
        this.moveLevel = moveLevel;
    }

}
