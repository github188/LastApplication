package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamCloudConverter;
import com.iermu.client.model.CamCloud;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhoushaopei on 15/7/21.
 */
public class CamCloudResponse extends Response {

    private List<CamCloud>  list;
    private int             count;

    public static CamCloudResponse parseResponse(String str) throws JSONException {
        CamCloudResponse response = new CamCloudResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    protected void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);

        this.count = json.optInt(Field.COUNT);
        JSONArray array = json.optJSONArray(Field.LIST);
        this.list = CamCloudConverter.fromJson(array);
    }

    public List<CamCloud> getList() {
        return list;
    }

    public void setList(List<CamCloud> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    class Field {
        public static final String COUNT = "count";
        public static final String LIST  = "list";
    }

}
