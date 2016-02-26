package com.iermu.client.business.api.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangxq on 15/8/11.
 */
public class StoreResponse extends Response {
    private String shareId;
    private String uk;
    private int shareType;

    /**
     * 解析JSON
     *
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.shareId = json.optString(Field.SHARE_ID);
        this.shareType = json.optInt(Field.SHARE_TYPE);
        this.uk = json.optString(Field.UK);
    }

    /**
     * 解析服务端响应
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static StoreResponse parseResponse(String str) throws JSONException {
        StoreResponse response = new StoreResponse();
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
    public static StoreResponse parseResponseError(Exception e) {
        StoreResponse response = new StoreResponse();
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

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    class Field {
        public static final String SHARE_ID = "shareid";
        public static final String UK = "uk";
        public static final String SHARE_TYPE = "share";
    }
}
