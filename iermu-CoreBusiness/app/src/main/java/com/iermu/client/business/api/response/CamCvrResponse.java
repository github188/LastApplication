package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamCronConverter;
import com.iermu.client.business.api.converter.CamCvrConverter;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamCvr;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  录制信息
 *
 * Created by wcy on 15/11/19.
 */
public class CamCvrResponse extends Response {

    private CamCvr camCvr;  //云录像信息

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CamCvrResponse parseResponse(String str) throws JSONException {
        CamCvrResponse response = new CamCvrResponse();
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
    public static CamCvrResponse parseResponseError(Exception e) {
        CamCvrResponse response = new CamCvrResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     * @param json
     */
    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        camCvr = CamCvrConverter.fromJson(json);
    }

    public CamCvr getCamCvr() {
        return camCvr;
    }
}
