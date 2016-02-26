package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamCloud;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 云录制服务状态数据转换器
 *
 * Created by zhoushaopei on 15/7/21.
 */
public class CamCloudConverter {

    /**
     * 解析JSONObject数据
     * @param json
     * @return
     */
    public static CamCloud fromJson(JSONObject json) {
        String deviceId     = json.optString(Field.DEVICE_ID);
        String streamId     = json.optString(Field.STREAM_ID);
        String description  = json.optString(Field.DESCRIPTION);
        int cvrDay          = json.optInt(Field.CVR_DAY);
        String expireTime   = json.optString(Field.EXPIRE_TIME);
        String appId        = json.optString(Field.APP_ID);
        int share           = json.optInt(Field.SHARE);
        int status          = json.optInt(Field.STATUS);
        String thumbnail    = json.optString(Field.THUMBNAIL);
        Date date           = new Date(Long.parseLong(json.optString(Field.END_TIME))*1000);



        CamCloud info = new CamCloud();
        info.setDeviceId(deviceId);
        info.setStreamId(streamId);
        info.setDescription(description);
        info.setCvrDay(cvrDay);
        info.setExpireTime(expireTime);
        info.setAppId(appId);
        info.setShare(share);
        info.setStatus(status);
        info.setThumbnail(thumbnail);
        info.setEndTime(date);
        return info;
    }

    /**
     * 解析JSONArray数据
     * @param array
     * @return
     * @throws JSONException
     */
    public static List<CamCloud> fromJson(JSONArray array) throws JSONException {
        ArrayList<CamCloud> list = new ArrayList<CamCloud>();
        if(array != null && array.length()>0) {
            for(int i=0; i<array.length(); i++) {
                CamCloud cloud = fromJson(array.getJSONObject(i));
                list.add(cloud);
            }
        }
        return list;
    }

    class Field {
        public static final String DEVICE_ID    = "deviceid";
        public static final String STREAM_ID    = "stream_id";
        public static final String DESCRIPTION  = "description";
        public static final String CVR_DAY      = "cvr_day";
        public static final String EXPIRE_TIME  = "expire_time";
        public static final String APP_ID       = "app_id";
        public static final String SHARE        = "share";
        public static final String STATUS       = "status";
        public static final String THUMBNAIL    = "thumbnail";
        public static final String END_TIME     = "cvr_end_time";

    }

    public String StrToDate(String str) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(new Date(Long.parseLong(str)*1000));
    }
}
