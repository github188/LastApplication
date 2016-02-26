package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamAlarmConverter;
import com.iermu.client.business.api.converter.CamCronConverter;
import com.iermu.client.business.api.converter.CamCvrConverter;
import com.iermu.client.business.api.converter.CamInfoConverter;
import com.iermu.client.business.api.converter.CamStatusConverter;
import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamCvr;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamStatus;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  获取摄像机设置全部信息
 *
 * Created by zhoushaopei on 15/8/3.
 */
public class CamSettingResponse extends Response {

    private CamInfo     camInfo;        //摄像机设备信息
    private CamStatus   camStatus;      //摄像机更多设置信息
    private CamCron     powerCron;      //摄像机定时信息
    private CamCvr      camCvr;        //摄像机云录制定时信息
    private CamAlarm    camAlarm;       //摄像机报警通知

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws JSONException
     */
    public static CamSettingResponse parseResponse(String str) throws JSONException {
        CamSettingResponse response = new CamSettingResponse();
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
    public static CamSettingResponse parseResponseError(Exception e) {
        CamSettingResponse response = new CamSettingResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     * @param json
     */
    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        JSONObject jsonInfo     = json.optJSONObject(Field.INFO);
        JSONObject jsonSetting  = json.optJSONObject(Field.STATUS);
        JSONObject jsonPower    = json.optJSONObject(Field.POWER);
        JSONObject jsonCvr      = json.optJSONObject(Field.CVR);
        JSONObject jsonAlarm    = json.optJSONObject(Field.ALARM);

        if (jsonInfo != null) {
            camInfo = CamInfoConverter.fromJson(jsonInfo);
        }
        if (jsonSetting != null) {
            camStatus = CamStatusConverter.fromJson(jsonSetting);
        }
        if (jsonPower != null) {
            powerCron = CamCronConverter.fromJsonAsPower(jsonPower);
        }
        if (jsonCvr != null) {
            camCvr = CamCvrConverter.fromJson(jsonCvr);
        }
        if (jsonAlarm != null) {
            camAlarm = CamAlarmConverter.fromJson(jsonAlarm);
        }
    }

    public CamCvr getCamCvr() {
        return camCvr;
    }

    public CamCron getPowerCron() {
        return powerCron;
    }

    public void setPowerCron(CamCron powerCron) {
        this.powerCron = powerCron;
    }

    public CamAlarm getCamAlarm() {
        return camAlarm;
    }

    public void setCamAlarm(CamAlarm camAlarm) {
        this.camAlarm = camAlarm;
    }

    public CamInfo getCamInfo() {
        return camInfo;
    }

    public void setCamInfo(CamInfo camInfo) {
        this.camInfo = camInfo;
    }

    public CamStatus getCamStatus() {
        return camStatus;
    }

    public void setCamStatus(CamStatus camStatus) {
        this.camStatus = camStatus;
    }

    class Field {
        public static final String INFO   = "info";
        public static final String STATUS = "status";
        public static final String POWER  = "power";
        public static final String CVR    = "cvr";
        public static final String ALARM  = "alarm";
    }
}
