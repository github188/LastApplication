package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamLive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的摄像机数据转换器
 * Created by wcy on 15/7/5.
 */
public class CamLiveConverter {

    /**
     * 解析JSON数据
     * @param array
     * @return
     */
    public static List<CamLive> fromJson(JSONArray array) throws JSONException {
        List<CamLive> list = new ArrayList<CamLive>();
        if(array != null) {
            for(int i=0; i<array.length(); i++) {
                CamLive mimeCam = fromJson(array.getJSONObject(i));
                if(mimeCam == null) continue;
                list.add(mimeCam);
            }
        }
        return list;
    }

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CamLive fromJson(JSONObject json) {
        String shareId      = json.optString(Field.SHARE_ID);
        String deviceId     = json.optString(Field.DEVICE_ID);
        String uk           = json.optString(Field.UK);
        String desc         = json.optString(Field.DESCRIPTION);
        int share           = json.optInt(Field.SHARE);
        int status          = json.optInt(Field.STATUS);
        String thumbnail    = json.optString(Field.THUMBNAIL);
        int dataType        = json.optInt(Field.DATATYPE);
        int connectType     = json.optInt(Field.CONNECT_TYPE);
        String connectCid   = json.optString(Field.CONNECT_CID);
        String streamId     = json.optString(Field.STREAM_ID);
        String cvrDay       = json.optString(Field.CVR_DAY);
        long cvrEndTime     = json.optLong(Field.CVR_ENDTIME);

        String ownerName    = json.optString(Field.OWNER_NAME);
        String avator       = json.optString(Field.AVATOR);
        int personNum       = json.optInt(Field.PERSON_NUM);
        int goodNum      = json.optInt(Field.GOOD_NUM);
        int storeStatus  = json.optInt(Field.STORE_STATUS);
        int grantNum       = json.optInt(Field.GRANT_NUM);
        int needupdate       = json.optInt(Field.NEED_UPDATE);
        int forceUpgrade       = json.optInt(Field.FORCE_UPGRADE);

        CamLive live = new CamLive();
        live.setShareId(shareId);
        live.setDeviceId(deviceId);
        live.setUk(uk);
        live.setDescription(desc);
        live.setShareType(share);
        live.setStatus(status);
        live.setThumbnail(thumbnail);
        live.setDataType(dataType);
        live.setConnectType(connectType);
        live.setConnectCid(connectCid);
        live.setStreamId(streamId);
        live.setCvrDay(cvrDay);
        live.setCvrEndTime(cvrEndTime);
        live.setAvator(avator);
        live.setOwnerName(ownerName);
        live.setPersonNum(personNum);
        live.setGoodNum(goodNum + "");
        live.setStoreStatus(storeStatus);
        live.setGrantNum(grantNum);
        live.setNeedupdate(needupdate);
        live.setForceUpgrade(forceUpgrade);
        return live;
    }

    class Field {
        public static final String SHARE_ID     = "shareid";
        public static final String DEVICE_ID    = "deviceid";
        public static final String UK           = "uk";
        public static final String DESCRIPTION  = "description";
        public static final String SHARE        = "share";
        public static final String STATUS       = "status";
        public static final String THUMBNAIL    = "thumbnail";
        public static final String DATATYPE     = "data_type";
        public static final String CONNECT_CID  = "connect_cid";
        public static final String CONNECT_TYPE = "connect_type";
        public static final String APP_ID       = "app_id";
        public static final String STREAM_ID    = "stream_id";
        public static final String CVR_DAY      = "cvr_day";
        public static final String CVR_ENDTIME  = "cvr_end_time";

        // 接口确定后需要修改，跟接口数据名同步
        public static final String OWNER_NAME   = "username";
        public static final String PERSON_NUM   = "viewnum";
        public static final String AVATOR       = "avatar";
        public static final String GOOD_NUM     = "approvenum";
        public static final String STORE_STATUS = "subscribe";
        public static final String GRANT_NUM    = "grantnum";
        public static final String NEED_UPDATE    = "need_upgrade";
        public static final String FORCE_UPGRADE    = "force_upgrade";
    }
}
