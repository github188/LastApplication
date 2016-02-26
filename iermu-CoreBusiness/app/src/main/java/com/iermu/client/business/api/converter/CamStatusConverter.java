package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamStatus;
import com.iermu.client.model.Email;

import org.json.JSONObject;

/**
 * 摄像机设置信息数据转换器
 *
 * Created by wcy on 15/7/8.
 */
public class CamStatusConverter {

    private static Email email;

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CamStatus fromJson(JSONObject json) {
        int power       = json.optInt(Field.power);
        int invert      = json.optInt(Field.invert);
        int audio       = json.optInt(Field.audio);
        int light       = json.optInt(Field.light);
        int localplay   = json.optInt(Field.localplay);
        int scene       = json.optInt(Field.scene);
        int nightmode   = json.optInt(Field.nightmode);
        int exposemode  = json.optInt(Field.exposemode);
        int bitlevel    = json.optInt(Field.bitlevel);
        int bitrate     = json.optInt(Field.bitrate);
        String maxspeed    = json.optString(Field.maxspeed);
        String minspeed    = json.optString(Field.minspeed);

        JSONObject jsonEmail    = json.optJSONObject("email");
        if (jsonEmail != null) {
            email = CamEmailConverter.fromJson(jsonEmail);
        }

        CamStatus setting = new CamStatus();
        setting.setPower(power == 1 ? true : false);
        setting.setInvert(invert == 1 ? true : false);
        setting.setAudio(audio == 1 ? true : false);
        setting.setLight(light == 1     ? true : false);
        setting.setLocalplay(localplay);
        setting.setScene(scene);
        setting.setNightmode(nightmode);
        setting.setExposemode(exposemode);
        setting.setBitlevel(bitlevel);
        setting.setBitrate(bitrate);
        setting.setMaxspeed(maxspeed);
        setting.setMinspeed(minspeed);
        setting.setEmail(email);
        return setting;
    }

    public static Email getEmail() {
        return email;
    }

    public static void setEmail(Email email) {
        CamStatusConverter.email = email;
    }

    class Field {
        public static final String power        = "power";
        public static final String light        = "light";
        public static final String invert       = "invert";
        public static final String audio        = "audio";
        public static final String localplay    = "localplay";
        public static final String scene        = "scene";
        public static final String nightmode    = "nightmode";
        public static final String exposemode   = "exposemode";
        public static final String bitlevel     = "bitlevel";
        public static final String bitrate      = "bitrate";
        public static final String maxspeed     = "maxspeed";
        public static final String minspeed     = "minspeed";
    }
}
