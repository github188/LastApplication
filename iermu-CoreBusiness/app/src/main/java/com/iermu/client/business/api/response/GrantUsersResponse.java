package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.GrantUserConverter;
import com.iermu.client.model.GrantUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 授权用户列表
 * Created by zhoushaopei on 15/7/13.
 */
public class GrantUsersResponse extends Response  {

    private List<GrantUser> list;

    private int count;

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static GrantUsersResponse parseResponse(String str) throws JSONException {
        GrantUsersResponse response = new GrantUsersResponse();
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
    public static GrantUsersResponse parseResponseError(Exception exception) {
        GrantUsersResponse response = new GrantUsersResponse();
        response.parseError(exception);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.count = json.optInt(Field.GRANT_COUNT);
        JSONArray jsonArray = json.getJSONArray(Field.GRANT_LIST);
        this.list = GrantUserConverter.fromJson(jsonArray);
    }

    class Field {
        public static final String GRANT_COUNT  = "count";
        public static final String GRANT_LIST   = "list";
    }


    public List<GrantUser> getList() {
        return list;
    }

    public void setList(List<GrantUser> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
