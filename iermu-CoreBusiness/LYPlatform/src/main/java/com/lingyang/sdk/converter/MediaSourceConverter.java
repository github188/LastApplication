package com.lingyang.sdk.converter;

import com.lingyang.sdk.model.MediaSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * Created by wcy on 15/8/4.
 */
public class MediaSourceConverter {


    /**
     * 解析JSON数据
     * @param array
     * @return
     */
    public static List<MediaSource> fromJson(JSONArray array) throws JSONException {
        ArrayList<MediaSource> list = new ArrayList<MediaSource>();
        if(array != null) {
            for(int i=0; i<array.length(); i++) {
                MediaSource source = fromJson(array.getJSONObject(i));
                if(source == null) continue;
                list.add(source);
            }
        }
        return list;
    }

    public static MediaSource fromJson(JSONObject json) {
        String id       = json.optString(Field.ID);
        boolean isBound = optBoolean(json, Field.ISBOUND);
        boolean isBroad = optBoolean(json, Field.ISBROAD);
        int natType     = json.optInt(Field.NATTYPE);
        int status      = json.optInt(Field.STATUS);

        MediaSource source = new MediaSource();
        source.setHashId(id);
        source.setBound(isBound);
        source.setBroad(isBroad);
        source.setNatType(natType);
        source.setStatus(status);
        return source;
    }

    private static boolean optBoolean(JSONObject json, String key) {
        return json.optInt(key)==1;
    }

    class Field {
        public static final String ID       = "ID";
        public static final String ISBOUND  = "IsBound";
        public static final String ISBROAD  = "IsBroad";
        public static final String NATTYPE  = "NatType";
        public static final String STATUS   = "Status";
    }
}
