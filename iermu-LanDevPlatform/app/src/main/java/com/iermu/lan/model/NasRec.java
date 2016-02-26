package com.iermu.lan.model;

/**
 * Created by zsj on 15/10/19.
 */
public class NasRec {

    private String stream_id;    //录像起始号
    private long start_time; //录像开始时间  int
    private long end_time;   //录像结束时间
    private boolean alarm;   // 报警录像
    private String timeLen;   //录像时间长度  // for cms rec
    private String fileLen;   //录像大小

    public String getStream_id() {
        return stream_id;
    }

    public void setStream_id(String stream_id) {
        this.stream_id = stream_id;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public String getTimeLen() {
        return timeLen;
    }

    public void setTimeLen(String timeLen) {
        this.timeLen = timeLen;
    }

    public String getFileLen() {
        return fileLen;
    }

    public void setFileLen(String fileLen) {
        this.fileLen = fileLen;
    }

    @Override
    public String toString() {
        return "NasRec{" +
                "stream_id='" + stream_id + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", alarm=" + alarm +
                ", timeLen='" + timeLen + '\'' +
                ", fileLen='" + fileLen + '\'' +
                '}';
    }
}
