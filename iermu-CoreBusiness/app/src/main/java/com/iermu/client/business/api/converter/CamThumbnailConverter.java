package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamThumbnail;
import com.iermu.client.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxq on 15/8/14.
 */
public class CamThumbnailConverter {
    /**
     * 解析JSON数据
     *
     * @param array
     * @return
     */
    public static List<CamThumbnail> fromJson(JSONArray array) throws JSONException {
        List<CamThumbnail> list = new ArrayList<CamThumbnail>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                CamThumbnail mimeCam = fromJson(array.getJSONObject(i));
                if (mimeCam == null) continue;
                list.add(mimeCam);
            }
        }
        return list;
    }

    /**
     * 解析lyy数据
     * @param array
     * @return
     * @throws JSONException
     */
    public static List<CamThumbnail> fromLYYJson(JSONArray array) throws  JSONException {
        List<CamThumbnail> list = new ArrayList<CamThumbnail>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                CamThumbnail mimeCam = fromLLYJson(array.getJSONObject(i));
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
    public static CamThumbnail fromJson(JSONObject json) {
        int time = json.optInt(Field.TIME) - DateUtil.TIME_ZONE_OFFECT;
        String url = json.optString(Field.URL);

        CamThumbnail thumbnail = new CamThumbnail();
        thumbnail.setTime(time);
        thumbnail.setUrl(url);
        return thumbnail;
    }

    /**
     * 解析LLYJSON数据
     *
     * @param json
     * @return
     */
    public static CamThumbnail fromLLYJson(JSONObject json) {
        int time = json.optInt(Field.TIME);
        String url = json.optString(Field.URL);

        CamThumbnail thumbnail = new CamThumbnail();
        thumbnail.setTime(time);
        thumbnail.setUrl(url);
        return thumbnail;
    }

    class Field {
        public static final String TIME = "time";
        public static final String URL = "url";
    }
}
