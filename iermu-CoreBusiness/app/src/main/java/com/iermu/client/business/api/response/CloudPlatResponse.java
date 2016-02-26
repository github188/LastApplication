package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamInfoConverter;
import com.iermu.client.business.api.converter.CloudPlatConverter;
import com.iermu.client.business.api.converter.ListPresetConverter;
import com.iermu.client.model.CloudPlat;
import com.iermu.client.model.CloudPreset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 获取云台信息
 *
 * Created by zhoushaopei on 15/7/13.
 */
public class CloudPlatResponse extends Response  {

    private CloudPlat cloudPlat;

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CloudPlatResponse parseResponse(String str) throws JSONException {
        CloudPlatResponse response = new CloudPlatResponse();
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
    public static CloudPlatResponse parseResponseError(Exception exception) {
        CloudPlatResponse response = new CloudPlatResponse();
        response.parseError(exception);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.cloudPlat = CloudPlatConverter.fromJson(json);
    }

    public CloudPlat getCloudPlat() {
        return cloudPlat;
    }

    public void setCloudPlat(CloudPlat cloudPlat) {
        this.cloudPlat = cloudPlat;
    }
}
