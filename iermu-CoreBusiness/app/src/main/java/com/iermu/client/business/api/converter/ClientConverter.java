package com.iermu.client.business.api.converter;

import com.iermu.client.model.NewPoster;

import org.json.JSONObject;

/**
 * Created by zhoushaopei on 15/11/26.
 */
public class ClientConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static NewPoster fromJson(JSONObject json) {
        NewPoster client  = new NewPoster();
        String imgUrl   = json.optString(Field.IMG_URL);
        String webUrl   = json.optString(Field.WEB_URL);
        long startTime  = json.optLong(Field.START_TIME);
        long endTime    = json.optLong(Field.END_TIME);

        client.setImgUrl(imgUrl);
        client.setWebUrl(webUrl);
        client.setStartTime(startTime);
        client.setEndTime(endTime);
        return client;
    }

    class Field {
        public static final String IMG_URL   = "imgurl";
        public static final String WEB_URL   = "weburl";
        public static final String START_TIME= "starttime";
        public static final String END_TIME  = "endtime";
    }
}
