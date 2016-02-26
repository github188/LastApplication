package com.iermu.client.business.api.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取AccessToken响应数据(百度)
 *
 * Created by wcy on 15/7/23.
 */
public class BaiduTokenResponse extends Response {

    private String  accessToken;    //AccessToken(百度)
    private String  refreshToken;   //RefreshToken(百度)
    private String  uid;            //用户ID
    private long    expiresIn;      //到期时间

    /**
     * 解析JSON
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.accessToken    = json.optString(Filed.ACCESS_TOKEN);
        this.refreshToken   = json.optString(Filed.REFRESH_TOKEN);
        this.uid            = json.optString(Filed.UID);
        this.expiresIn      = json.optLong(Filed.EXPIRES_IN);
    }

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static BaiduTokenResponse parseResponse(String str) throws JSONException {
        BaiduTokenResponse response = new BaiduTokenResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     * @param e
     * @return
     */
    public static BaiduTokenResponse parseResponseError(Exception e) {
        BaiduTokenResponse response = new BaiduTokenResponse();
        response.parseError(e);
        return response;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    class Filed {
        public static final String ACCESS_TOKEN  = "access_token";
        public static final String REFRESH_TOKEN = "refresh_token";
        public static final String UID           = "uid";
        public static final String EXPIRES_IN    = "expires_in";
    }

}
