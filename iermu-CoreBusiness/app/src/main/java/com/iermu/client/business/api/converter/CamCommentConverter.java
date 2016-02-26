package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamComment;
import com.iermu.client.model.CamLive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxq on 15/8/3.
 */
public class CamCommentConverter {
    /**
     * 解析JSON数据
     *
     * @param array
     * @return
     */
    public static List<CamComment> fromJson(JSONArray array) throws JSONException {
        List<CamComment> list = new ArrayList<CamComment>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                CamComment mimeCam = fromJson(array.getJSONObject(i));
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
    public static CamComment fromJson(JSONObject json) {
        String cId = json.optString(Field.CID);
        String parentId = json.optString(Field.PARENT_ID);
        String ip = json.optString(Field.IP);
        String avator = json.optString(Field.AVATOR);
        String ownerName = json.optString(Field.OWNER_NAME);
        String date = json.optString(Field.DATELINE);
        String content = json.optString(Field.CONTENT);
        String uid = json.optString(Field.UID);

        return new CamComment(cId, parentId, ip, avator, ownerName, date, content, uid);
    }

    class Field {
        public static final String AVATOR = "avatar";
        public static final String OWNER_NAME = "username";
        public static final String CONTENT = "comment";
        public static final String IP = "ip";
        public static final String CID = "cid";
        public static final String PARENT_ID = "parent_cid";
        public static final String UID = "uid";
        public static final String DATELINE = "dateline";
    }
}
