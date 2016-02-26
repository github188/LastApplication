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
public class CamPowerCronResponse extends Response {

    private CamCron powerCron;  //摄像机定时信息

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CamPowerCronResponse parseResponse(String str) throws JSONException {
        CamPowerCronResponse response = new CamPowerCronResponse();
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
    public static CamPowerCronResponse parseResponseError(Exception e) {
        CamPowerCronResponse response = new CamPowerCronResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     * @param json
     */
    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        powerCron = CamCronConverter.fromJsonAsPower(json);
    }


    public CamCron getPowerCron() {
        return powerCron;
    }

    public void setPowerCron(CamCron powerCron) {
        this.powerCron = powerCron;
    }

}
