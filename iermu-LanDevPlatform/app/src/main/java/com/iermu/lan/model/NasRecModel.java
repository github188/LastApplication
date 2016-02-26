package com.iermu.lan.model;

/**
 * Created by richard on 15/10/19.
 */
public class NasRecModel {

    private int rec_start_id;      //录像起始号
    private String rec_date;       //日期  int
    private byte rec_type;         //录像类型(报警1,普通-0)
    private int rec_cms_time;      //录像开始时间（经过cms算法压缩）
    private String rec_start_date; //录像开始日期
    private String rec_start_time; //录像开始时间  int
    private String rec_time_len;   //录像时间长度
    private String rec_end_time;   //录像结束时间
    private String rec_file_len;   //录像大小
    private int rec_ch_id;         //录像通道信息
    private int rec_hdd_id;        //硬盘号
    private int rec_lba_id;	       // LBA
    public boolean rec_enabled = false;   // 是否选中录像


    public int getRec_start_id() {
        return rec_start_id;
    }

    public void setRec_start_id(int rec_start_id) {
        this.rec_start_id = rec_start_id;
    }

    public String getRec_date() {
        return rec_date;
    }

    public void setRec_date(String rec_date) {
        this.rec_date = rec_date;
    }

    public byte getRec_type() {
        return rec_type;
    }

    public void setRec_type(byte rec_type) {
        this.rec_type = rec_type;
    }

    public int getRec_cms_time() {
        return rec_cms_time;
    }

    public void setRec_cms_time(int rec_cms_time) {
        this.rec_cms_time = rec_cms_time;
    }

    public String getRec_start_date() {
        return rec_start_date;
    }

    public void setRec_start_date(String rec_start_date) {
        this.rec_start_date = rec_start_date;
    }

    public String getRec_start_time() {
        return rec_start_time;
    }

    public void setRec_start_time(String rec_start_time) {
        this.rec_start_time = rec_start_time;
    }

    public String getRec_time_len() {
        return rec_time_len;
    }

    public void setRec_time_len(String rec_time_len) {
        this.rec_time_len = rec_time_len;
    }

    public String getRec_end_time() {
        return rec_end_time;
    }

    public void setRec_end_time(String rec_end_time) {
        this.rec_end_time = rec_end_time;
    }

    public String getRec_file_len() {
        return rec_file_len;
    }

    public void setRec_file_len(String rec_file_len) {
        this.rec_file_len = rec_file_len;
    }

    public int getRec_ch_id() {
        return rec_ch_id;
    }

    public void setRec_ch_id(int rec_ch_id) {
        this.rec_ch_id = rec_ch_id;
    }

    public int getRec_hdd_id() {
        return rec_hdd_id;
    }

    public void setRec_hdd_id(int rec_hdd_id) {
        this.rec_hdd_id = rec_hdd_id;
    }

    public int getRec_lba_id() {
        return rec_lba_id;
    }

    public void setRec_lba_id(int rec_lba_id) {
        this.rec_lba_id = rec_lba_id;
    }

    public boolean isRec_enabled() {
        return rec_enabled;
    }

    public void setRec_enabled(boolean rec_enabled) {
        this.rec_enabled = rec_enabled;
    }
}
