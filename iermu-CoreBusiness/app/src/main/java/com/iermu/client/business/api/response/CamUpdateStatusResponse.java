package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamAlarmConverter;
import com.iermu.client.business.api.converter.CamUpdateStatusConverter;
import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.viewmodel.CamUpdateStatus;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zsj on 16/1/4.
 */
public class CamUpdateStatusResponse extends Response {

    private CamUpdateStatus updateStatus;


    public static CamUpdateStatusResponse parseResponse(String str) throws JSONException {
        CamUpdateStatusResponse response = new CamUpdateStatusResponse();
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
    public static CamUpdateStatusResponse parseResponseError(Exception e) {
        CamUpdateStatusResponse response = new CamUpdateStatusResponse();
        response.parseError(e);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        updateStatus = CamUpdateStatusConverter.fromJson(json);
    }

    public CamUpdateStatus getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(CamUpdateStatus updateStatus) {
        this.updateStatus = updateStatus;
    }
}
