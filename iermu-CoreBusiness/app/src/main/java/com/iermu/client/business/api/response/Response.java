package com.iermu.client.business.api.response;


import android.text.TextUtils;

import com.iermu.client.business.impl.event.OnAbortAccountEvent;
import com.iermu.client.business.impl.event.OnBaiduTokenInvalidEvent;
import com.iermu.client.model.Business;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.Logger;
import com.iermu.eventobj.BusObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RetrofitError;

public class Response {

    private int     statusCode; //HTTP响应状态码
    private int     errorCode;  //错误码
    private String  errorMsg;   //错误信息
    private long    requestId;  //请求ID(时间戳)
    private int  connectType;//百度请求

    private Business business;

    public Response() {
        this.errorCode = ErrorCode.UNDEFINED;
    }

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static Response parseResponse(String str) throws JSONException {
        Response response = new Response();
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
    public static Response parseResponseError(Exception e) {
        Response response = new Response();
        response.parseError(e);
        return response;
    }

    /**
     * 解析服务端响应异常错误
     * @param e
     */
    protected void parseError(Exception e) {
        try {
            if(e instanceof RetrofitError && e!=null && ((RetrofitError) e).getBody()!=null) {
                RetrofitError error = (RetrofitError) e;
                String body     = error.getBody().toString();
                JSONObject json = new JSONObject(body);
                this.errorCode  = json.optInt(Field.ERROR_CODE, ErrorCode.NETWORK_ERROR);
                this.errorMsg   = json.optString(Field.ERROR_MSG);
                this.requestId  = json.optLong(Field.REQUEST_ID);
                this.connectType= json.optInt(Field.CONNECT_TYPE, 0);
            } else if(e instanceof Exception) {
                Logger.i("请求发生异常");
                this.errorCode = ErrorCode.NETWORK_ERROR;
            }
            this.business = Business.parseResponseError(this, e);
        } catch (JSONException e1) {
            e1.printStackTrace();
            this.business = Business.parseResponseError(this, e);
        } finally {
            //110:Token失效  50201:服务器出错 //{"error_code":50201, "error_msg":"api closed"}
            //110: 密码被修改|账号登录异常
            //111: Token失效
            if(errorCode == ErrorCode.ACCESS_TOKEN_INVALID && connectType == ConnectType.BAIDU) {
                BusObject.publishEvent(OnBaiduTokenInvalidEvent.class, business);
            } else if(errorCode == ErrorCode.ACCESS_TOKEN_INVALID || errorCode==ErrorCode.API_CLOSED) {
                BusObject.publishEvent(OnAbortAccountEvent.class, business);
            }
        }
    }

    /**
     * 解析Json数据
     * @param json
     */
    protected void parseJson(JSONObject json) throws JSONException {
        this.errorCode  = json.optInt(Field.ERROR_CODE, ErrorCode.SUCCESS);
        this.errorMsg   = json.optString(Field.ERROR_MSG);
        this.requestId  = json.optLong(Field.REQUEST_ID);
        this.business   = Business.parseResponseError(this);
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getRequestId() {
        return requestId;
    }

    public Business getBusiness() {
        return business;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    class Field {
        public static final String ERROR_CODE   = "error_code";
        public static final String ERROR_MSG    = "error_msg";
        public static final String REQUEST_ID   = "request_id";
        public static final String CONNECT_TYPE = "connect_type";
    }

}