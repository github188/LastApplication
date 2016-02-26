package com.iermu.client.business.api.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangxq on 15/8/11.
 */
public class FavourResponse extends Response {
    private int favourNum;

    /**
     * 解析JSON
     *
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.favourNum = json.optInt(Field.FAVOUR_NUM);
    }

    /**
     * 解析服务端响应
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static FavourResponse parseResponse(String str) throws JSONException {
        FavourResponse response = new FavourResponse();
        if (!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     *
     * @param e
     * @return
     */
    public static FavourResponse parseResponseError(Exception e) {
        FavourResponse response = new FavourResponse();
        response.parseError(e);
        return response;
    }

    public int getFavourNum() {
        return favourNum;
    }

    public void setFavourNum(int favourNum) {
        this.favourNum = favourNum;
    }

    class Field {
        public static final String FAVOUR_NUM = "approvenum";
    }
}
