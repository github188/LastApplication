package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamCronConverter;
import com.iermu.client.model.CamCron;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  摄像机定时信息(摄像机定时、录像定时、报警定时)
 *
 * Created by wcy on 15/7/1.
 */
public class CamCronResponse extends Response {

    private CamCron powerCron;  //摄像机定时信息
    private CamCron cvrCron;    //摄像机录像定时
    private CamCron alarmCron;  //摄像机报警定时

    private boolean isCvr;      //是否启用云录制

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CamCronResponse parseResponse(String str) throws JSONException {
        CamCronResponse response = new CamCronResponse();
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
    public static CamCronResponse parseResponseError(Exception e) {
        CamCronResponse response = new CamCronResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     * @param json
     */
    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        JSONObject powerObj = json.optJSONObject("power");
        JSONObject cvrObj = json.optJSONObject("cvr");
        JSONObject alarmObj = json.optJSONObject("alarm");
        if(powerObj != null) {
            powerCron   = CamCronConverter.fromJson(powerObj);
        }
        if(cvrObj != null) {
            isCvr   = cvrObj.optInt("cvr", 0)==1; //解析云录像设置开关 (服务器将字段放在该位置解析)
            cvrCron = CamCronConverter.fromJson(cvrObj);
        }
        if(alarmObj != null) {//报警定时信息: 不走这里解析 (@see com.iermu.client.business.api.response.CamAlarmResponse)
            alarmCron   = CamCronConverter.fromJson(alarmObj);
        }
    }

    public boolean isCvr() {
        return isCvr;
    }

    public CamCron getPowerCron() {
        return powerCron;
    }

    public void setPowerCron(CamCron powerCron) {
        this.powerCron = powerCron;
    }

    public CamCron getCvrCron() {
        return cvrCron;
    }

    public void setCvrCron(CamCron cvrCron) {
        this.cvrCron = cvrCron;
    }

    public CamCron getAlarmCron() {
        return alarmCron;
    }

    public void setAlarmCron(CamCron alarmCron) {
        this.alarmCron = alarmCron;
    }

}
