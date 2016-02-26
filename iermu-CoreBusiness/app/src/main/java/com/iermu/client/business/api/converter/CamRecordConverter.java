package com.iermu.client.business.api.converter;

import com.iermu.client.util.DateUtil;
import com.iermu.lan.model.CamRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxq on 15/8/13.
 */
public class CamRecordConverter {
    /**
     * 解析JSON数据
     * @param array
     * @return
     */
    public static List<CamRecord> fromJsonArray(JSONArray array) throws JSONException {
        List<CamRecord> list = new ArrayList<CamRecord>();
        if(array != null) {
            for(int i=0; i<array.length(); i++) {

                CamRecord mimeCam = fromJson(array.getJSONArray(i));
                if(mimeCam == null) continue;
                list.add(mimeCam);
            }
        }
        return list;
    }

    public static List<CamRecord> fromLyyJsonArray(JSONArray array, String diskInfo) throws JSONException {
        List<CamRecord> list = new ArrayList<CamRecord>();
        if(array != null) {
            for(int i=0; i<array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                int start = object.optInt(Field.FROM);
                int end = object.optInt(Field.TO);

                CamRecord record = new CamRecord();
                record.setStartTime(start);
                record.setEndTime(end);
                record.setDiskInfo(diskInfo);
                list.add(record);
            }
        }
        return list;
    }

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CamRecord fromJson(JSONArray json) throws JSONException {
        int startTime = json.getInt(0) - DateUtil.TIME_ZONE_OFFECT;
        int endTime = json.getInt(1) - DateUtil.TIME_ZONE_OFFECT;
        CamRecord record = new CamRecord();
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        return record;
    }

    class Field {
        public static final String FROM = "from";
        public static final String TO = "to";
    }
}
