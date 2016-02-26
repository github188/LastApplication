package com.iermu.client.business.api.converter;

import com.iermu.client.model.CloudPreset;
import com.iermu.client.model.GrantUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *  云台预置点列表
 *
 * Created by zhoushaopei on 15/8/13.
 */
public class ListPresetConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CloudPreset fromJson(JSONObject json) throws JSONException {

        int preset      = json.optInt(Field.PRESET);
        String title    = json.optString(Field.TITLE);

        CloudPreset cloudPreset = new CloudPreset();
        cloudPreset.setPreset(preset);
        cloudPreset.setTitle(title);

        return cloudPreset;
    }

    /**
     * 解析JSONArray数据
     * @param array
     * @return
     * @throws org.json.JSONException
     */
    public static List<CloudPreset> fromJson(JSONArray array) throws JSONException {
        ArrayList<CloudPreset> list = new ArrayList<CloudPreset>();
        if(array != null && array.length()>0) {
            for(int i=0; i<array.length(); i++) {
                CloudPreset preset = fromJson(array.getJSONObject(i));
                list.add(preset);
            }
        }
        return list;
    }

    class Field {
        public static final String PRESET       = "preset";
        public static final String TITLE        = "title";

    }
}
