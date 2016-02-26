package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamInfo;

import org.json.JSONObject;

/**
 * 摄像机设置信息数据转换器
 *
 * Created by wcy on 15/7/8.
 */
public class CamInfoConverter {


    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CamInfo fromJson( JSONObject json) {
        String deviceId = json.optString(Field.ID);
        String intro    = json.optString(Field.INTRO);
        String model    = json.optString(Field.MODEL);
        String namePlate= json.optString(Field.NAMEPLATE);
        String resolution= json.optString(Field.RESOLUTION);
        String firmware = json.optString(Field.FIRMWARE);
        String firmdate = json.optString(Field.FIRMDATE);
        String ip       = json.optString(Field.IP);
        String mac      = json.optString(Field.MAC);
        String sn       = json.optString(Field.SN);
        String sig      = json.optString(Field.SIG);
        String wifi     = json.optString(Field.WIFI);
        String platform = json.optString(Field.PLATFORM);

        CamInfo camInfo = new CamInfo();
        camInfo.setId(deviceId);
        camInfo.setIntro(intro);
        camInfo.setModel(model);
        camInfo.setNamePlate(namePlate);
        camInfo.setResolution(resolution);
        camInfo.setFirmware(firmware);
        camInfo.setFirmdate(firmdate);
        camInfo.setIp(ip);
        camInfo.setMac(mac);
        camInfo.setSn(sn);
        camInfo.setSig(sig);
        camInfo.setWifi(wifi);
        camInfo.setPlatform(platform);
        return camInfo;
    }

    class Field {
        public static final String ID       = "id";
        public static final String INTRO    = "intro";
        public static final String MODEL    = "model";
        public static final String NAMEPLATE= "nameplate";
        public static final String RESOLUTION= "resolution";
        public static final String SN       = "sn";
        public static final String MAC      = "mac";
        public static final String WIFI     = "wifi";
        public static final String IP       = "ip";
        public static final String SIG      = "sig";
        public static final String FIRMWARE = "firmware";
        public static final String FIRMDATE = "firmdate";
        public static final String PLATFORM = "platform";
    }
}
