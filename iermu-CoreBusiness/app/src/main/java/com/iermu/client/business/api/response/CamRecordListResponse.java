package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamRecordConverter;
import com.iermu.lan.model.CamRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhangxq on 15/8/13.
 */
public class CamRecordListResponse extends Response {
    private List<CamRecord> records;

    /**
     * 解析服务端响应
     *
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CamRecordListResponse parseResponse(String str) throws JSONException {
        CamRecordListResponse response = new CamRecordListResponse();
        if (!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析羚羊云录像列表数据
     * @param str
     * @return
     * @throws JSONException
     */
    public static CamRecordListResponse parseLyyResponse(String str) throws JSONException {
        CamRecordListResponse response = new CamRecordListResponse();
        if (!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseLyyJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     *
     * @param e
     * @return
     */
    public static CamRecordListResponse parseResponseError(Exception e) {
        CamRecordListResponse response = new CamRecordListResponse();
        response.parseError(e);
        return response;
    }

    public void parseLyyJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        JSONObject object = json.getJSONObject(Field.RESULTS);
        JSONArray array = object.getJSONArray(Field.VIDEOS);
        String diskInfo = object.toString();
        this.records = CamRecordConverter.fromLyyJsonArray(array, diskInfo);
    }

    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        JSONArray array = json.optJSONArray(Field.RESULTS);
        this.records = CamRecordConverter.fromJsonArray(array);
    }

    public List<CamRecord> getRecords() {
        return records;
    }

    public void setRecords(List<CamRecord> records) {
        this.records = records;
    }

    class Field {
        public static final String RESULTS = "results";
        public static final String VIDEOS = "videos";
    }
}
