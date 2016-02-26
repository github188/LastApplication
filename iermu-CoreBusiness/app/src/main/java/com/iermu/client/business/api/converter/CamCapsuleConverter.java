package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamCapsule;
import com.iermu.client.model.CamInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 摄像机设置信息数据转换器
 *
 * Created by wcy on 15/7/8.
 */
public class CamCapsuleConverter {


    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CamCapsule fromJson( JSONObject json) throws JSONException {
        double temperature = json.getLong(Field.TEMPERATURE);
        double humidity    = json.getLong(Field.HUMIDITY);

        CamCapsule camCapsule = new CamCapsule();
        camCapsule.setTemperature(temperature);
        camCapsule.setHumidity(humidity);
        return camCapsule;
    }

    class Field {
        public static final String TEMPERATURE  = "temperature";
        public static final String HUMIDITY     = "humidity";
    }
}
