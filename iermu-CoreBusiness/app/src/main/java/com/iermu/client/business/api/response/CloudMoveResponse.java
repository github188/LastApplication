package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CloudPlatConverter;
import com.iermu.client.model.CloudPlat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取云台信息
 *
 * Created by zhoushaopei on 15/7/13.
 */
public class CloudMoveResponse extends Response  {

    private String result; // 最后一位数转换为二进制，左边届、右边届、上边界、下边界，从右到左，“1”：到边界｜“0”：未到边界

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CloudMoveResponse parseResponse(String str) throws JSONException {
        CloudMoveResponse response = new CloudMoveResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     * @param exception
     * @return
     */
    public static CloudMoveResponse parseResponseError(Exception exception) {
        CloudMoveResponse response = new CloudMoveResponse();
        response.parseError(exception);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.result = json.getString(Field.RESULT);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    class Field {
        public static final String RESULT = "result";
    }
}
