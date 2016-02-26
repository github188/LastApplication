package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.viewmodel.CamUpdateStatus;

import org.json.JSONObject;

/**
 * 获取摄像机升级状态
 *
 * Created by zsj on 16/1/4.
 */
public class CamUpdateStatusConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CamUpdateStatus fromJson(JSONObject json) {
        CamUpdateStatus updateStatus = new CamUpdateStatus();
        int status      = json.optInt(Field.STATUS);
        String deviceid      = json.optString(Field.DEVICEDID);

        updateStatus.setIntStatus(status);
        updateStatus.setDeviceid(deviceid);
        return updateStatus;
    }

    class Field {
        public static final String STATUS     = "status";
        public static final String DEVICEDID     = "deviceid";
    }
}
