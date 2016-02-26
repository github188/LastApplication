package com.iermu.client.business.api.converter;

import com.iermu.client.model.GrantUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *  获取授权用户列表
 *
 * Created by zhoushaopei on 15/8/13.
 */
public class GrantUserConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static GrantUser fromJson(JSONObject json) throws JSONException {

        String uk       = json.optString(Field.UK);
        String name     = json.optString(Field.NAME);
        String authCode = json.optString(Field.AUTH_CODE);
        String time     = json.optString(Field.TIME);

        GrantUser grant = new GrantUser();
        grant.setUk(uk);
        grant.setName(name);
        grant.setAuthCode(authCode);
        grant.setTime(time);

        return grant;
    }

    /**
     * 解析JSONArray数据
     * @param array
     * @return
     * @throws JSONException
     */
    public static List<GrantUser> fromJson(JSONArray array) throws JSONException {
        ArrayList<GrantUser> list = new ArrayList<GrantUser>();
        if(array != null && array.length()>0) {
            for(int i=0; i<array.length(); i++) {
                GrantUser grant = fromJson(array.getJSONObject(i));
                list.add(grant);
            }
        }
        return list;
    }

    class Field {
        public static final String UK        = "uk";
        public static final String NAME      = "name";
        public static final String AUTH_CODE = "auth_code";
        public static final String TIME      = "time";

    }
}
