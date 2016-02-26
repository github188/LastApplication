package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamInfoConverter;
import com.iermu.client.model.CamInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  获取摄像机信息
 *
 * Created by wcy on 15/7/1.
 */
public class CamInfoResponse extends Response {

    private CamInfo camInfo;   //摄像机设备信息

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CamInfoResponse parseResponse(String str) throws JSONException {
        CamInfoResponse response = new CamInfoResponse();
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
    public static CamInfoResponse parseResponseError(Exception e) {
        CamInfoResponse response = new CamInfoResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     * @param json
     */
    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        camInfo = CamInfoConverter.fromJson(json);
    }

    public CamInfo getCamInfo() {
        return camInfo;
    }

    public void setCamInfo(CamInfo camInfo) {
        this.camInfo = camInfo;
    }

}
