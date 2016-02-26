package com.iermu.client.model;

/**
 * 录像缩略图模型
 *
 * Created by zhangxq on 15/8/13.
 */
public class CamThumbnail {
    private int time;
    private String url;

    public CamThumbnail() {

    }

    public CamThumbnail(int time, String url) {
        this.time = time;
        this.url = url;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
