package com.iermu.client.model;

import java.util.StringTokenizer;

/**
 * Created by zhangxq on 15/8/3.
 */
public class CamComment {
    private String cId;
    private String parentId;
    private String ip;
    private String avator;
    private String ownerName;
    private String date;
    private String content;
    private String uid;
    private int localId;

    public CamComment() {

    }

    public CamComment(String cId, String parentId, String ip, String avator, String ownerName, String date, String content, String uid) {
        this.cId = cId;
        this.parentId = parentId;
        this.ip = ip;
        this.avator = avator;
        this.ownerName = ownerName;
        this.date = date;
        this.content = content;
        this.uid = uid;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }
}
