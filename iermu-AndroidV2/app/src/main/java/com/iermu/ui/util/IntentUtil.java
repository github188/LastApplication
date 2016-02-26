package com.iermu.ui.util;

import android.content.Intent;
import android.text.TextUtils;

import com.iermu.client.config.AppConfig;

/**
 * Created by wcy on 16/1/29.
 */
public class IntentUtil {

    /**
     * 验证Intent是否需要绑定账号
     *  1.验证青岛联通参数是否合格
     *  2.验证青岛联通是否登录状态
     *
     * @param intent
     * @return
     */
    public static boolean isIntentFromQDLT(Intent intent) {                 //青岛联通
        if(intent == null
                || !intent.hasExtra("xxSecret")
                || !intent.hasExtra("xxToken")
                || !intent.hasExtra("xxParam")) {
            return false;
        }
        String xxSecret= intent.getStringExtra("xxSecret");
        String xxToken = intent.getStringExtra("xxToken");
        byte[] xxParam = intent.getByteArrayExtra("xxParam");

        com.unicom.oa.MD5 md5 = new com.unicom.oa.MD5();
        String decry    = md5.putMd5para(xxParam, AppConfig.QDLT_TOKEN);
        if(!decry.contains("&")
                || TextUtils.isEmpty(decry)
                || decry.length() <= decry.indexOf("&")
                || TextUtils.isEmpty(decry.substring(0, decry.indexOf("&"))) ) {
            return false;
        }
        String result   = decry.substring(0, decry.indexOf("&"));
        String md5Code  = md5.GetMD5Code(xxSecret + AppConfig.QDLT_TOKEN + result);
        return xxToken.equals(md5Code);
    }



}
