package com.iermu.client.business.api.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangxq on 15/11/17.
 */
public class VodSeekResponse extends Response {
    private int trueStartTime;
    private int oldStartTime;
    private int trueEndTime;

    /**
     * 解析JSON
     *
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.trueStartTime = json.optInt(Field.TRUE_STARTTIME);
        this.oldStartTime = json.optInt(Field.OLD_STARTTIME);
        this.trueEndTime = json.optInt(Field.TRUE_ENDTIME);
    }

    /**
     * 解析服务端响应
     *
     * @param str
     * @return
     * @throws JSONException
     */
    public static VodSeekResponse parseResponse(String str) throws JSONException {
        VodSeekResponse response = new VodSeekResponse();
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
    public static VodSeekResponse parseResponseError(Exception e) {
        VodSeekResponse response = new VodSeekResponse();
        response.parseError(e);
        return response;
    }

    public int getTrueStartTime() {
        return trueStartTime;
    }

    public void setTrueStartTime(int trueStartTime) {
        this.trueStartTime = trueStartTime;
    }

    public int getOldStartTime() {
        return oldStartTime;
    }

    public void setOldStartTime(int oldStartTime) {
        this.oldStartTime = oldStartTime;
    }

    public int getTrueEndTime() {
        return trueEndTime;
    }

    public void setTrueEndTime(int trueEndTime) {
        this.trueEndTime = trueEndTime;
    }

    class Field {
        public static final String TRUE_STARTTIME = "start_time";
        public static final String OLD_STARTTIME = "t";
        public static final String TRUE_ENDTIME = "end_time";
    }
}
