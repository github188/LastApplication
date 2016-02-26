package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.ConnectConverter;
import com.iermu.client.business.api.converter.UserInfoConverter;
import com.iermu.client.model.Connect;
import com.iermu.client.model.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 用户信息
 * Created by xjy on 15/8/27.
 */
public class UserInfoResponse extends  Response {

    private UserInfo info;     //个人信息
    private List<Connect> connectList;

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws JSONException
     */
    public static UserInfoResponse parseResponse(String str) throws JSONException {
        UserInfoResponse response = new UserInfoResponse();
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
    public static UserInfoResponse parseResponseError(Exception e) {
        UserInfoResponse response = new UserInfoResponse();
        response.parseError(e);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        info = UserInfoConverter.fromJson(json);
        JSONArray array = json.optJSONArray(Field.CONNECT);
        this.connectList = ConnectConverter.fromJson(array);
    }

    public UserInfo getInfo() {
        return info;
    }

    public List<Connect> getConnectList() {
        return connectList;
    }

    class Field {
        public static final String CONNECT = "connect";
    }
}
