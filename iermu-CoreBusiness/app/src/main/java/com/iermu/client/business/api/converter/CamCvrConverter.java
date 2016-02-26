package com.iermu.client.business.api.converter;

import android.text.TextUtils;

import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamCvr;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.util.DateUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhoushaopei on 15/11/19.
 */
public class CamCvrConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CamCvr fromJson(JSONObject json) {
        int cvr = json.optInt(Field.CVR);
        CamCron cron = CamCronConverter.fromJson(json);
        CamCvr camCvr = new CamCvr();
        camCvr.setCvr((cvr==1));
        camCvr.setCron(cron);
        return camCvr;
    }

    class Field {
        public static final String CVR  = "cvr";
    }
}