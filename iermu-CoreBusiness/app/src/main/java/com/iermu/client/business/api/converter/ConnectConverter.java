package com.iermu.client.business.api.converter;

import com.iermu.client.model.Connect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 平台信息数据转换器
 *
 * Created by wcy on 15/7/31.
 */
public class ConnectConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static Connect fromJson(JSONObject json) {
        int type    = json.optInt(Field.CONNECT_TYPE);
        String uid  = json.optString(Field.CONNECT_UID);
        int status  = json.optInt(Field.STATUS);
        String cId  = json.optString(Field.CONNECT_CID);
        String userToken    = json.optString(Field.USER_TOKEN);
        String userConfig   = json.optString(Field.USER_CONFIG);
        String accessToken  = json.optString(Field.ACCESS_TOKEN);
        String refreshToken = json.optString(Field.REFRESH_TOKEN);
        String userName = json.optString(Field.USER_NAME);
        String secret   = json.optString(Field.CONNECT_SECRET);

        Connect connect = new Connect();
        connect.setConnectType(type);
        connect.setUid(uid);
        connect.setUserName(userName);
        connect.setStatus(status);
        connect.setCid(cId);
        connect.setUserToken(userToken);
        connect.setUserConfig(userConfig);
        connect.setAccessToken(accessToken);
        connect.setRefreshToken(refreshToken);
        return connect;
    }

    /**
     * 解析JSON数据
     * @param array
     * @return
     */
    public static List<Connect> fromJson(JSONArray array) throws JSONException {
        List<Connect> list = new ArrayList<Connect>();
        if(array != null) {
            for(int i=0; i<array.length(); i++) {
                Connect connect = fromJson(array.getJSONObject(i));
                if(connect == null) continue;
                list.add(connect);
            }
        }
        return list;
    }

    class Field {
        public static final String CONNECT_TYPE     = "connect_type";
        public static final String CONNECT_UID      = "connect_uid";
        public static final String USER_NAME        = "username";
        public static final String CONNECT_SECRET   = "connect_secret";
        public static final String STATUS           = "status";
        public static final String ACCESS_TOKEN     = "access_token";
        public static final String REFRESH_TOKEN    = "refresh_token";
        public static final String CONNECT_CID      = "connect_cid";
        public static final String USER_TOKEN       = "user_token";
        public static final String USER_CONFIG      = "init";
    }

}
