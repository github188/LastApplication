package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamAlarmConverter;
import com.iermu.client.business.api.converter.CamCronConverter;
import com.iermu.client.business.api.converter.CamInfoConverter;
import com.iermu.client.business.api.converter.CamStatusConverter;
import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamStatus;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  获取摄像机设置全部信息
 *
 * Created by zhoushaopei on 15/8/3.
 */
public class CamStatusResponse extends Response {

    private CamStatus   camStatus;      //摄像机更多设置信息

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CamStatusResponse parseResponse(String str) throws JSONException {
        CamStatusResponse response = new CamStatusResponse();
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
    public static CamStatusResponse parseResponseError(Exception e) {
        CamStatusResponse response = new CamStatusResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     * @param json
     */
    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        camStatus = CamStatusConverter.fromJson(json);
    }


    public CamStatus getCamStatus() {
        return camStatus;
    }

    public void setCamStatus(CamStatus camStatus) {
        this.camStatus = camStatus;
    }
}
