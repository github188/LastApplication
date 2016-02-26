package com.iermu.client.business.api.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangxq on 15/8/11.
 */
public class CreateShareResponse extends Response {
    private String shareId;
    private String uk;

    /**
     * 解析JSON
     *
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.shareId = json.optString(Field.SHARE_ID);
        this.uk = json.optString(Field.UK);
    }

    /**
     * 解析服务端响应
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static CreateShareResponse parseResponse(String str) throws JSONException {
        CreateShareResponse response = new CreateShareResponse();
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
    public static CreateShareResponse parseResponseError(Exception e) {
        CreateShareResponse response = new CreateShareResponse();
        response.parseError(e);
        return response;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getUk() {
        return uk;
    }

    public void setUk(String uk) {
        this.uk = uk;
    }

    class Field {
        public static final String SHARE_ID = "shareid";
        public static final String UK = "uk";
    }
}
