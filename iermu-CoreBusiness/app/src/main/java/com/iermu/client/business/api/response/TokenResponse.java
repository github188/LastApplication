package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.ConnectConverter;
import com.iermu.client.model.Connect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 获取AccessToken响应数据(服务器)
 * <p/>
 * Created by wcy on 15/7/23.
 */
public class TokenResponse extends Response {

    private String accessToken;    //AccessToken(爱耳目服务器)
    private String refreshToken;   //RefreshToken(爱耳目服务器)
    private String uid;            //用户ID
    private long expiresIn;      //到期时间
    private List<Connect> connectList;

    /**
     * 解析JSON
     *
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.accessToken = json.optString(Field.ACCESS_TOKEN);
        this.refreshToken = json.optString(Field.REFRESH_TOKEN);
        this.uid = json.optString(Field.UID);
        this.expiresIn = json.optLong(Field.EXPIRES_IN);
        JSONArray array = json.optJSONArray(Field.CONNECT);
        this.connectList = ConnectConverter.fromJson(array);
    }

    /**
     * 解析服务端响应
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static TokenResponse parseResponse(String str) throws JSONException {
        TokenResponse response = new TokenResponse();
        if (!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     *
     * @param e
     * @return
     */
    public static TokenResponse parseResponseError(Exception e) {
        TokenResponse response = new TokenResponse();
        response.parseError(e);
        return response;
    }

    public List<Connect> getConnectList() {
        return connectList;
    }

    public void setConnectList(List<Connect> connectList) {
        this.connectList = connectList;
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

    class Field {
        public static final String ACCESS_TOKEN = "access_token";
        public static final String REFRESH_TOKEN = "refresh_token";
        public static final String UID = "uid";
        public static final String EXPIRES_IN = "expires_in";
        public static final String CONNECT = "connect";
    }

}
