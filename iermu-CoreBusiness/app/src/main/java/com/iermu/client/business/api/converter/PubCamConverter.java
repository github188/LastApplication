package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamLive;
import com.iermu.client.test.PubCamInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的摄像机数据转换器
 * Created by wcy on 15/7/5.
 */
public class PubCamConverter {

    /**
     * 解析JSON数据
     *
     * @param array
     * @return
     */
    public static List<PubCamInfo> fromJson(JSONArray array) throws JSONException {
        List<PubCamInfo> list = new ArrayList<PubCamInfo>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                PubCamInfo mimeCam = fromJson(array.getJSONObject(i));
                if (mimeCam == null) continue;
                list.add(mimeCam);
            }
        }
        return list;
    }

    /**
     * 解析JSON数据
     *
     * @param json
     * @return
     */
    public static PubCamInfo fromJson(JSONObject json) {
        String name     = json.optString(Field.NAME);
        String status   = json.optString(Field.STATUS);
        String imageUrl = json.optString(Field.IMAGE_URL);
        int num         = json.optInt(Field.NUM);
        String where    = json.optString(Field.WHERE);
        return new PubCamInfo(name, status, imageUrl, num, where);
    }

    class Field {
        public static final String NAME = "name";
        public static final String STATUS = "status";
        public static final String IMAGE_URL = "imageUrl";
        public static final String NUM = "num";
        public static final String WHERE = "where";
    }
}
