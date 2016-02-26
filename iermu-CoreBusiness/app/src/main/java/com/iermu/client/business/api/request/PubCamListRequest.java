package com.iermu.client.business.api.request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangxq on 15/7/31.
 */
public class PubCamListRequest extends Request {

    private String method; // 方法名
    private String sign; // Sign 由 APPID=1, AK, RealSign 三个元素以及’-’组成; RealSign 为 appid, expire, AK, SK 四个元素组成的字符串计算得到的 md5 ; 其中AK,SK为开发者在开放平台申请的应用的AK,SK。
    private int expire; // 签名串的过期时间
    private int page; // 获取公共分享设备的起始位置
    private int count; // 获取公共摄像头的个数,最多为100
    private int category; // 公开分类id
    private int orderby; // 排序类型，hot为最热，new为最新，recommend为推荐

    public PubCamListRequest(String sign, int expire, int page, int count, int orderby) {
        this.method = "listshare";
        this.sign = sign;
        this.expire = expire;
        this.page = page;
        this.count = count;
        this.orderby = orderby;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Field.METHOD, method);
        json.put(Field.SIGN, sign);
        json.put(Field.EXPIRE, expire);
        json.put(Field.PAGE, page);
        json.put(Field.COUNT, count);
        json.put(Field.ORDERBY, orderby);
        return json;
    }

    class Field {
        public static final String METHOD = "method";
        public static final String SIGN = "sign";
        public static final String EXPIRE = "expire";
        public static final String PAGE = "page";
        public static final String COUNT = "count";
        public static final String ORDERBY = "orderby";
    }
}
