package com.iermu.client.model;

import java.io.Serializable;

/**
 * Created by zhoushaopei on 15/11/26.
 */
public class NewPoster implements Serializable {

    private String imgUrl;
    private String webUrl;
    private long startTime;
    private long endTime;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
