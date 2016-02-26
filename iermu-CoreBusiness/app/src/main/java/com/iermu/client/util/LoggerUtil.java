package com.iermu.client.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by xjy on 15/7/10.
 */
public class LoggerUtil {

    /**
     *  打印服务器返回错误
     * @param error
     */
    public static void error(RetrofitError error) {
        if(error == null) return;
        Response response = error.getResponse();
        String stackTraceString = Log.getStackTraceString(error);
        int status = 0;
        String reason = "";
        try {
            if(response != null) {
                status = response.getStatus();
                reason	= response.getReason();
                TypedInput body = response.getBody();
                byte[] bodyBytes = ((TypedByteArray)body).getBytes();
                String bodyMime = body.mimeType();
                String bodyCharset = MimeUtil.parseCharset(bodyMime, "UTF-8");
                String bodyStr = new String(bodyBytes, bodyCharset);
                Logger.e("<-- HTTP Error"
                        + "\r\n" + response.getUrl()
                        + "\r\n" + bodyStr);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Logger.e("<-- HTTP Error Url: "+error.getUrl()+" Status: "+status+" Reason: "+reason
                + "\r\n"+ stackTraceString);
    }

    /**
     * 打印异常
     * @param message
     * @param e
     */
    public static void e(String message, Exception e) {
        if(e instanceof RetrofitError) {
            LoggerUtil.error((RetrofitError) e);
        } else {
            Logger.e(message, e);
        }
    }
}
