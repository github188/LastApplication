package com.iermu.lan.model;

import java.io.Serializable;

/**
 * Created by richard on 15/11/12.
 */
public class CardInforResult implements Serializable {

//    String st = util.getDateFromByte(cmsGet.bParams, 0); // 录像开始时间
//    String et = util.getDateFromByte(cmsGet.bParams, 4); // 录像结束时间
//    int iCoverCount = util.ByteArrayToint(cmsGet.bParams, 8); // 覆盖次数
//    byte bHddNum = cmsGet.bParams[12];  // sd卡个数， 0:没有sd卡  >0: 接sd卡
//    int iTotalNum = bHddNum==0? 0 : util.ByteArrayToint(cmsGet.bParams, 20);  // 总容量  单位：10M
//    int iRemainNum = bHddNum==0? 0 : util.ByteArrayToint(cmsGet.bParams, 24); // 剩余容量 单位：10M
//    int iBadNum = bHddNum==0? 0 : util.ByteArrayToint(cmsGet.bParams, 28);    // 坏块数   单位：8K
//    String sBadNum = bHddNum==0? "0" : Float.toString(((float) (iBadNum)) / 8f); // bad block



    private ErrorCode errorCode;
    private String st;
    private String et;
    private int iCoverCount;
    private byte bHddNum;
    private int iRemainNum;
    private int iTotalNum;
    private int iBadNum;
    private String sBadNum;

    public CardInforResult(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CardInforResult(ErrorCode errorCode, String st, String et, int iCoverCount, byte bHddNum, int iTotalNum, int iRemainNum, int iBadNum, String sBadNum) {
        this.errorCode = errorCode;
        this.st = st;
        this.et = et;
        this.iCoverCount = iCoverCount;
        this.bHddNum = bHddNum;
        this.iRemainNum = iRemainNum;
        this.iTotalNum = iTotalNum;
        this.iBadNum = iBadNum;
        this.sBadNum = sBadNum;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getEt() {
        return et;
    }

    public void setEt(String et) {
        this.et = et;
    }

    public int getiCoverCount() {
        return iCoverCount;
    }

    public void setiCoverCount(int iCoverCount) {
        this.iCoverCount = iCoverCount;
    }

    public byte getbHddNum() {
        return bHddNum;
    }

    public void setbHddNum(byte bHddNum) {
        this.bHddNum = bHddNum;
    }

    public int getiRemainNum() {
        return iRemainNum;
    }

    public void setiRemainNum(int iRemainNum) {
        this.iRemainNum = iRemainNum;
    }

    public int getiTotalNum() {
        return iTotalNum;
    }

    public void setiTotalNum(int iTotalNum) {
        this.iTotalNum = iTotalNum;
    }

    public int getiBadNum() {
        return iBadNum;
    }

    public void setiBadNum(int iBadNum) {
        this.iBadNum = iBadNum;
    }

    public String getsBadNum() {
        return sBadNum;
    }

    public void setsBadNum(String sBadNum) {
        this.sBadNum = sBadNum;
    }

    @Override
    public String toString() {
        return "CardInforResult{" +
                "errorCode=" + errorCode +
                ", st='" + st + '\'' +
                ", et='" + et + '\'' +
                ", iCoverCount=" + iCoverCount +
                ", bHddNum=" + bHddNum +
                ", iRemainNum=" + iRemainNum +
                ", iTotalNum=" + iTotalNum +
                ", iBadNum=" + iBadNum +
                ", sBadNum='" + sBadNum + '\'' +
                '}';
    }
}
