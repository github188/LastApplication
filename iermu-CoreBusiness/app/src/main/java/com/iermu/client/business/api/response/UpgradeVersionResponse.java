package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.UpgradeVersionConverter;
import com.iermu.client.model.UpgradeVersion;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zsj on 16/1/14.
 */
public class UpgradeVersionResponse extends Response {

    private UpgradeVersion upgradeVersion;


    public static UpgradeVersionResponse parseResponse(String str) throws JSONException {
        UpgradeVersionResponse response = new UpgradeVersionResponse();
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
    public static UpgradeVersionResponse parseResponseError(Exception e) {
        UpgradeVersionResponse response = new UpgradeVersionResponse();
        response.parseError(e);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        upgradeVersion = UpgradeVersionConverter.fromJson(json);
    }

    public UpgradeVersion getUpdateStatus() {
        return upgradeVersion;
    }

    public void setUpdateStatus(UpgradeVersion upgradeVersion) {
        this.upgradeVersion = upgradeVersion;
    }
}
