package com.iermu.client.business.api.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhoushaopei on 15/8/13.
 */
public class GetGrantCodeResponse extends Response {

    private String code;

    /**
     * 解析服务端响应
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static GetGrantCodeResponse parseResponse(String str) throws JSONException {
        GetGrantCodeResponse response = new GetGrantCodeResponse();
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
    public static GetGrantCodeResponse parseResponseError(Exception e) {
        GetGrantCodeResponse response = new GetGrantCodeResponse();
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
        this.code = json.optString(Field.CODE);
    }

    class Field {
        public static final String CODE = "code";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
