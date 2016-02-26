package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.CamCommentConverter;
import com.iermu.client.model.CamComment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangxq on 15/8/5.
 */
public class CommentSendResponse extends Response {
    private int count;
    private CamComment comment;

    /**
     * 解析JSON
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.count = json.optInt(Field.COUNT);
        JSONObject object = json.optJSONObject(Field.COMMENT);
        this.comment = CamCommentConverter.fromJson(object);
    }

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws JSONException
     */
    public static CommentSendResponse parseResponse(String str) throws JSONException {
        CommentSendResponse response = new CommentSendResponse();
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
    public static CommentSendResponse parseResponseError(Exception e) {
        CommentSendResponse response = new CommentSendResponse();
        response.parseError(e);
        return response;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public CamComment getComment() {
        return comment;
    }

    public void setComment(CamComment comment) {
        this.comment = comment;
    }

    class Field {
        public static final String COUNT = "commentnum";
        public static final String COMMENT = "comment";
    }
}
