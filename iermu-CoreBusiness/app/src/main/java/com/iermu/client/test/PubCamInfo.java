package com.iermu.client.test;

/**
 * Created by zhoushaopei on 15/6/24.
 */
public class PubCamInfo {

    private String name;
    private String status;
    private String imageUrl;
    private int num;
    private String where;

    public PubCamInfo() {

    }

    public PubCamInfo(String name, String status, String imageUrl, int num, String where) {
        this.name = name;
        this.status = status;
        this.imageUrl = imageUrl;
        this.num = num;
        this.where = where;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
