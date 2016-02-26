package com.iermu.client.business.api.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhoushaopei on 15/8/14.
 */
public class GrantShareResponse extends Response {

    private String uid;
    private String user_name;


    /**
     * 解析服务端响应
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static GrantShareResponse parseResponse(String str) throws JSONException {
        GrantShareResponse response = new GrantShareResponse();
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
    public static GrantShareResponse parseResponseError(Exception e) {
        GrantShareResponse response = new GrantShareResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     *
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.uid = json.optString(Field.UID);
        this.user_name = json.optString(Field.USER_NAME);
    }

    class Field {
        public static final String UID = "uid";
        public static final String USER_NAME= "username";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

}

