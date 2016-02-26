package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamThumbnailConverter;
import com.iermu.client.model.CamThumbnail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhangxq on 15/8/14.
 */
public class CamThumbnailListResponse extends Response {
    private int count;
    private List<CamThumbnail> list;

    /**
     * 解析JSON
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.count = json.optInt(Field.COUNT);
        JSONArray array = json.optJSONArray(Field.LIST);
        this.list = CamThumbnailConverter.fromJson(array);
    }

    /**
     * 解析LLYJSON
     * @param json
     */
    public void parseLLYJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.count = json.optInt(Field.COUNT);
        JSONArray array = json.optJSONArray(Field.LIST);
        this.list = CamThumbnailConverter.fromLYYJson(array);
    }

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws JSONException
     */
    public static CamThumbnailListResponse parseResponse(String str) throws JSONException {
        CamThumbnailListResponse response = new CamThumbnailListResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析lyy数据
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static CamThumbnailListResponse parseLYYResponse(String str) throws  JSONException {
        CamThumbnailListResponse response = new CamThumbnailListResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseLLYJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     * @param e
     * @return
     */
    public static CamThumbnailListResponse parseResponseError(Exception e) {
        CamThumbnailListResponse response = new CamThumbnailListResponse();
        response.parseError(e);
        return response;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CamThumbnail> getList() {
        return list;
    }

    public void setList(List<CamThumbnail> list) {
        this.list = list;
    }

    class Field {
        public static final String COUNT = "count";
        public static final String LIST = "list";
    }
}
