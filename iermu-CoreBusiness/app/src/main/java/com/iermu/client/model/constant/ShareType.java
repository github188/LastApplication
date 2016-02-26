package com.iermu.client.model.constant;

/**
 * 分享类型
 *
 * Created by wcy on 15/7/29.
 */
public class ShareType {

    public static final int PRIVATE      = 0;   //未分享(自己的设备)
    public static final int PUB_NOTCLOUD = 1;   //公开分享不带录像
    public static final int PRI_NOTCLOUD = 2;   //私密分享不带录像

    public static final int PUB_HAVCLOUD = 3;   //公开分享带录像
    public static final int PRI_HAVCLOUD = 4;   //私密分享带录像

}
