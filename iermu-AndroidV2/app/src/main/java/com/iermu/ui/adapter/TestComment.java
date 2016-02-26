package com.iermu.ui.adapter;

/**
 * Created by zhangxq on 15/7/22.
 */
public class TestComment {
    private boolean isMine;
    private String content;
    private String avatorUrl;
    private String name;
    private String date;

    public boolean isMine() {
        return isMine;
    }

    public TestComment(boolean isMine, String content, String avatorUrl, String name, String date) {
        this.isMine = isMine;
        this.content = content;
        this.avatorUrl = avatorUrl;
        this.name = name;
        this.date = date;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatorUrl() {
        return avatorUrl;
    }

    public void setAvatorUrl(String avatorUrl) {
        this.avatorUrl = avatorUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
