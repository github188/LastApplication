package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamCommentConverter;
import com.iermu.client.model.CamComment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxq on 15/8/3.
 */
public class CommentListResponse extends Response {
    private int count;
    private List<CamComment> list = new ArrayList<CamComment>();

    /**
     * 解析JSON
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.count = json.optJSONObject(Field.PAGE).optInt(Field.TOTAL);
        JSONArray array = json.optJSONArray(Field.LIST);
        this.list = CamCommentConverter.fromJson(array);
    }

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws JSONException
     */
    public static CommentListResponse parseResponse(String str) throws JSONException {
        CommentListResponse response = new CommentListResponse();
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
    public static CommentListResponse parseResponseError(Exception e) {
        CommentListResponse response = new CommentListResponse();
        response.parseError(e);
        return response;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CamComment> getList() {
        return list;
    }

    public void setList(List<CamComment> list) {
        this.list = list;
    }

    class Field {
        public static final String LIST = "list";
        public static final String PAGE = "page";
        public static final String TOTAL = "total";
    }
}
