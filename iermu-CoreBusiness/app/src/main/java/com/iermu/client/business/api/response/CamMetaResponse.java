package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamLiveConverter;
import com.iermu.client.model.CamLive;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取摄像机直播信息响应数据
 *
 * Created by wcy on 15/7/22.
 */
public class CamMetaResponse extends Response {

    private CamLive camLive;

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CamMetaResponse parseResponse(String str) throws JSONException {
        CamMetaResponse response = new CamMetaResponse();
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
    public static CamMetaResponse parseResponseError(Exception e) {
        CamMetaResponse response = new CamMetaResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        camLive = CamLiveConverter.fromJson(json);
    }

    public CamLive getCamLive() {
        return camLive;
    }

    public void setCamLive(CamLive camLive) {
        this.camLive = camLive;
    }
}
