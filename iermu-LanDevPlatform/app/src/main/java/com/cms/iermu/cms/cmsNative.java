package com.cms.iermu.cms;

import android.util.Log;

import java.util.logging.Logger;

/**
 * Created by wcy on 15/6/22.
 */
public class cmsNative {

    static {
        try {
            System.loadLibrary("cmsNative");
        } catch (UnsatisfiedLinkError e) {
            Log.i("load library failed", e.toString());
        }
    }

    public static native String getAppInfo(String strIn);

    public static native String getAppInfoA(String strIn);

    public static native String getAppInfoS(String strIn);

    public static native String getIpcLogPwd(String strIn);

    public static native String getIpcApPwd(String strIn);

    // 获取pcs权限服务器sk，固定值
    public static native String getPcsTokenS(String strIn);

    public static native int pcm2adpcm(byte[] inBuf, int inLen, byte[] outBuf);

    public static native int cmsCrcGet(byte[] inBuf, int inLen, long iPolynomial);


}
