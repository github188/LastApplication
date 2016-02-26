package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamAlarmConverter;
import com.iermu.client.model.CamAlarm;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhoushaopei on 15/7/21.
 */
public class CamAlarmResponse extends Response {

    private CamAlarm alarm;


    public static CamAlarmResponse parseResponse(String str) throws JSONException {
        CamAlarmResponse response = new CamAlarmResponse();
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
    public static CamAlarmResponse parseResponseError(Exception e) {
        CamAlarmResponse response = new CamAlarmResponse();
        response.parseError(e);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        alarm = CamAlarmConverter.fromJson(json);
    }
    public CamAlarm getAlarm() {
        return alarm;
    }

    public void setAlarm(CamAlarm alarm) {
        this.alarm = alarm;
    }
}
