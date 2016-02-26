package com.iermu.client.model;

/**
 * 云台预置点
 *
 * Created by zhoushaopei on 15/10/19.
 */
public class CloudPreset {

    private int preset;     //云台预置点序号
    private String title;   //云台预置点位置

    public void setPreset(int preset) {
        this.preset = preset;
    }

    public int getPreset() {
        return preset;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
