package com.iermu.client.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by zhoushaopei on 15/12/11.
 */
public class DeviceUtil {

    /**
     * 获取手机型号
     * @return
     */
    public static String getPhoneType() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机版本号
     * @return
     */
    public static String getPhoneVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机分辨率（注：4.0系统之前获取分辨率的方式和4.0之后获取分辨率的方式不同）
     * @param context
     * @return
     */
    public static String getPhoneDisplay(Context context) {
        Display mDisplay = ((Activity)context).getWindowManager().getDefaultDisplay();
        int W = mDisplay.getWidth();
        int H = mDisplay.getHeight();
        if (TextUtils.isEmpty(String.valueOf(W) + String.valueOf(H))) {
            DisplayMetrics mDisplayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
            int width = mDisplayMetrics.widthPixels;
            int height = mDisplayMetrics.heightPixels;
            return String.valueOf(width) + "x" + String.valueOf(height);
        } else {
            return String.valueOf(W) + "x" + String.valueOf(H);
        }
    }
}
