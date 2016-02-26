package com.iermu.client.business.api;

import android.util.Log;

import com.cms.iermu.cms.CmsErr;
import com.iermu.client.config.ApiConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jack on 16/1/15.
 */
public class ClipApi {

    static final String TAG = ApiConfig.class.getSimpleName();

    /**
     * 检查是否有进行中的任务api url
     */
    public static  String getClipStatusUrl(String accessToken, String deviceId, boolean bTask){
        String url = ApiConfig.PCS_URL_DEV
                +"?method=infoclip"
                + "&access_token=" + accessToken
                + "&deviceid=" + deviceId
                + "&type=" + (bTask? "task" : "quota");
        return url;
    }



    /**
     * 检查是否有进行中的任务
     * @param strAccessToken
     * @param strDevID
     * @param bTask
     * @param pcsErr
     * @return
     */
    public static String getClipStatus(String strAccessToken, String strDevID, boolean bTask, CmsErr pcsErr){

        String ret = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "infoclip");
        params.put("access_token", strAccessToken);
        params.put("deviceid", strDevID);
        params.put("type", bTask? "task" : "quota");
        Log.d(TAG, "clip search status start");
        try {
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil
                    .sendGetRequest(ApiConfig.PCS_URL_DEV, params, ApiConfig.getHeaders());
            int retCode = conn.getResponseCode();
            if (retCode == 200) {
                // 读取服务器返回信息
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                pcsErr.setErrValue(retCode, responseData);
                ret = responseData;
            }
            else { // 失败怎样处理？
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getErrorStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                pcsErr.setErrValue(-1, responseData);
                ret = responseData;
                Log.d(TAG, "ret msg: " + conn.getResponseMessage() + ", ret stream is:" + responseData);
            }
            conn.disconnect();// 终止连接
        } catch (Exception e) {
            Log.d(TAG, "timeout");
            pcsErr.setErrValue(-3, e.getMessage());
            e.printStackTrace();
        }
        Log.d(TAG, "ret ok!");
        return ret;
    }

    //检测文件名是否重复
    public static boolean isExistFile(String strAccessToken, String strName, CmsErr pcsErr){

        boolean ret = false;
        String strPath = "/apps/iermu/clip/" + strName + ".mp4";
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "meta");
        params.put("access_token", strAccessToken);
        params.put("path", strPath);
        Log.d(TAG, "get file is exist start");
        try {
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil
                    .sendGetRequest(ApiConfig.PCS_URL_FILE, params, ApiConfig.getHeaders());
            int retCode = conn.getResponseCode();
            if (retCode == 200) {
                // 读取服务器返回信息
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                Log.d(TAG, "ret msg: " + responseData);
                pcsErr.setErrValue(0, "ok");
                ret = (responseData.indexOf("fs_id")<0)? false : true;
            }
            else { // 失败怎样处理？
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getErrorStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                pcsErr.setErrValue(-1, responseData);
                Log.d(TAG, "ret msg: " + conn.getResponseMessage() + ", ret stream is:" + responseData);
            }
            conn.disconnect();// 终止连接
        } catch (Exception e) {
            pcsErr.setErrValue(-2, "timeout");
            Log.d(TAG, "get file info timeout");
            e.printStackTrace();
        }
        Log.d(TAG, "ret ok!");
        return ret;
    }

    /**
     * 开始剪辑录像
     * @param strAccessToken
     * @param strDevID
     * @param lSt 剪辑开始时间
     * @param lEt 结束时间
     * @param strClipName 剪辑文件名称
     * @param pcsErr 错误返回
     */
    public static boolean startClip(String strAccessToken, String strDevID, long lSt, long lEt, String strClipName, CmsErr pcsErr){

        boolean ret = false;

        String strSt = Long.toString(lSt);
        String strEt = Long.toString(lEt);

        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "clip");
        params.put("access_token", strAccessToken);
        params.put("deviceid", strDevID);
        if(strClipName!=null && !strClipName.equals("")) params.put("name", strClipName);
        if(strSt!=null && strEt!=null){
            params.put("st", strSt);
            params.put("et", strEt);
        }
        Log.d(TAG, "clip start: " + strDevID + ", st=" + strSt + ", et=" + strEt);
        try {
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil
                    .sendGetRequest(ApiConfig.PCS_URL_DEV, params, ApiConfig.getHeaders());
            int retCode = conn.getResponseCode();
            if (retCode == 200) {
                // 读取服务器返回信息
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                ret = true;
            }
            else { // 失败怎样处理？
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getErrorStream()));
                String retData = null;
                String responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
                JSONObject retMsg = new JSONObject(responseData);
                int iErrCode = retMsg.getInt("error_code");
                String strMsg = retMsg.getString("error_msg");
                pcsErr.setErrValue(iErrCode, strMsg);
                Log.d(TAG, "ret msg: " + conn.getResponseMessage() + ", ret stream is:" + responseData);
            }
            conn.disconnect();// 终止连接
        } catch (Exception e) {
            pcsErr.setErrValue(-3, e.getMessage());
            e.printStackTrace();
        }
        Log.d(TAG, "ret ok!");
        return ret;
    }
}
