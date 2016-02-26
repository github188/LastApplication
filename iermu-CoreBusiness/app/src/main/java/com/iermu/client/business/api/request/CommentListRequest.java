package com.iermu.client.business.api.request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangxq on 15/8/3.
 */
public class CommentListRequest extends Request {

    private String method;      // 方法名
    private String deviceId;    // 设备ID
    private int page;           // 页数，默认为1
    private int count;          // 每页数据条数，默认为10

    public CommentListRequest(String deviceId, int page, int count) {
        this.method = "listshare";
        this.deviceId = deviceId;
        this.page = page;
        this.count = count;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Field.METHOD, method);
        json.put(Field.DEVICE_ID, deviceId);
        json.put(Field.PAGE, page);
        json.put(Field.COUNT, count);
        return json;
    }

    class Field {
        public static final String METHOD = "method";
        public static final String DEVICE_ID = "sign";
        public static final String PAGE = "expire";
        public static final String COUNT = "start";
    }
}
