package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.GrantUserConverter;
import com.iermu.client.business.api.converter.ListPresetConverter;
import com.iermu.client.model.CloudPreset;
import com.iermu.client.model.GrantUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 授权用户列表
 * Created by zhoushaopei on 15/7/13.
 */
public class CloudListPresetResponse extends Response  {

    private List<CloudPreset> list;

    private int count;

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static CloudListPresetResponse parseResponse(String str) throws JSONException {
        CloudListPresetResponse response = new CloudListPresetResponse();
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
    public static CloudListPresetResponse parseResponseError(Exception exception) {
        CloudListPresetResponse response = new CloudListPresetResponse();
        response.parseError(exception);
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.count = json.optInt(Field.GRANT_COUNT);
        JSONArray jsonArray = json.getJSONArray(Field.GRANT_LIST);
        this.list = ListPresetConverter.fromJson(jsonArray);
    }

    class Field {
        public static final String GRANT_COUNT  = "count";
        public static final String GRANT_LIST   = "list";
    }


    public List<CloudPreset> getList() {
        return list;
    }

    public void setList(List<CloudPreset> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
