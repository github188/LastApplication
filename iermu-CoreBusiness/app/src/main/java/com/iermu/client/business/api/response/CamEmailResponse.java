package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamEmailConverter;
import com.iermu.client.model.Email;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  获取摄像机Email配置信息
 *
 * Created by wcy on 15/7/1.
 */
public class CamEmailResponse extends Response {

    private Email email;   //摄像机设备Email配置信息

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CamEmailResponse parseResponse(String str) throws JSONException {
        CamEmailResponse response = new CamEmailResponse();
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
    public static CamEmailResponse parseResponseError(Exception e) {
        CamEmailResponse response = new CamEmailResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     * @param json
     */
    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        email = CamEmailConverter.fromJson(json);
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }
}
