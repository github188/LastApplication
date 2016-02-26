package com.iermu.client.business.api.request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 我的摄像机列表请求参数
 *
 * Created by wcy on 15/6/27.
 */
public class MimeCamListRequest extends Request {

    private String  method;     //函数名
    private int     deviceType; //设备类型,默认为1, 预留字段
    private String  dataType;   //数据类型, 默认为'my'我的设备;'grant'授权设备,'sub'订阅设备,'all'所有以上
    private String  listType;   //列表类型, 默认为'all'所有设备, 'page'分页返回
    private int     page;       //页数, 默认为1, listType为page时有效
    private int     count;      //每页数据条数, 默认为10, listType为page时有效
    private String accessToken; //

    /**
     * @param deviceType
     * @param page
     */
    public MimeCamListRequest(int deviceType, int page) {
        this.method = "list";
        this.accessToken = "1a10b2cb631d1087f67ec8f68922feca";//"3a10b2cb631d1087f67ec8f68922feca";//accessToken;
        this.deviceType = deviceType;
        this.dataType   = "all";
        this.listType   = "page";
        this.page       = (page<=0) ? 1 : page;
        this.count      = 10;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Field.METHOD, method);
        json.put(Field.DEVICE_TYPE, deviceType);
        json.put(Field.DATA_TYPE, dataType);
        json.put(Field.LIST_TYPE, listType);
        json.put(Field.PAGE, page);
        json.put(Field.COUNT, count);
        json.put(Field.ACCESS_TOKEN, accessToken);
        return json;
    }

    class Field {
        public static final String METHOD       = "method";
        public static final String DEVICE_TYPE  = "device_type";
        public static final String DATA_TYPE    = "data_type";
        public static final String LIST_TYPE    = "list_type";
        public static final String PAGE         = "page";
        public static final String COUNT        = "count";
        public static final String ACCESS_TOKEN = "access_token";
    }

}
