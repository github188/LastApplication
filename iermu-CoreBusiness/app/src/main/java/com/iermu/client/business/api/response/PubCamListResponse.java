package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamLiveConverter;
import com.iermu.client.business.api.converter.PubCamConverter;
import com.iermu.client.model.CamLive;
import com.iermu.client.test.PubCamInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 公共摄像头列表响应
 *
 * Created by wcy on 15/6/26.
 */
public class PubCamListResponse extends Response {

    private int count;
    private int totalNum;
    private int currentPageNum;
    private int nextPageNum;
    private List<CamLive> list = new ArrayList<CamLive>();

    /**
     * 解析JSON
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.count = json.optInt(Field.COUNT);
        JSONArray array = json.optJSONArray(Field.LIST);
        JSONObject jsonObject = json.optJSONObject(Field.PAGE);
        this.totalNum = jsonObject.optInt(Field.TOTAL);
        this.currentPageNum = jsonObject.optInt(Field.CURRENT_PAGE);
        this.nextPageNum = jsonObject.optInt(Field.NEXT_PAGE);
        this.list = CamLiveConverter.fromJson(array);
    }

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws JSONException
     */
    public static PubCamListResponse parseResponse(String str) throws JSONException {
        PubCamListResponse response = new PubCamListResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     * @param e
     * @return
     */
    public static PubCamListResponse parseResponseError(Exception e) {
        PubCamListResponse response = new PubCamListResponse();
        response.parseError(e);
        return response;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CamLive> getList() {
        return list;
    }

    public void setList(List<CamLive> list) {
        this.list = list;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public int getNextPageNum() {
        return nextPageNum;
    }

    public void setNextPageNum(int nextPageNum) {
        this.nextPageNum = nextPageNum;
    }

    class Field {
        public static final String COUNT = "count";
        public static final String LIST = "device_list";
        public static final String PAGE = "page";
        public static final String TOTAL = "total";
        public static final String CURRENT_PAGE = "current";
        public static final String NEXT_PAGE = "next";
    }
}
