package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamCapsuleConverter;
import com.iermu.client.business.api.converter.CamInfoConverter;
import com.iermu.client.business.api.converter.CloudPlatConverter;
import com.iermu.client.model.CamCapsule;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CloudPlat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取摄像机温湿度
 *
 * Created by zhoushaopei on 15/7/13.
 */
public class CapsuleResponse extends Response  {

    public CamCapsule camCapsule;

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CapsuleResponse parseResponse(String str) throws JSONException {
        CapsuleResponse response = new CapsuleResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     * @param exception
     * @return
     */
    public static CapsuleResponse parseResponseError(Exception exception) {
        CapsuleResponse response = new CapsuleResponse();
        response.parseError(exception);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        camCapsule = CamCapsuleConverter.fromJson(json);
    }

    public CamCapsule getCamCapsule() {
        return camCapsule;
    }

    public void setCamCapsule(CamCapsule camCapsule) {
        this.camCapsule = camCapsule;
    }
}
