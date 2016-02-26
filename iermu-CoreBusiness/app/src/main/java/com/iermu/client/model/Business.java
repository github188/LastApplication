package com.iermu.client.model;

import android.util.Log;

import com.iermu.client.business.api.response.ErrorCode;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.BusinessMsg;
import com.iermu.client.util.Logger;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit.RetrofitError;

/**
 * 业务响应状态码、信息
 *
 * Created by wcy on 15/7/24.
 */
public class Business {

    private int     code;       //业务层状态码
    private String  message;    //业务状态码对应消息
    private int     errorCode;  //服务器返回错误码
    private String  errorMsg;   //服务器返回错误信息
    private long    requestId;  //服务器返回请求ID
    private int     connectType;//平台类型（百度：1、羚羊：2）

    /**
     * 业务请求是否成功
     * @return
     */
    public boolean isSuccess() {
        return code == BusinessCode.SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public static Business parseResponseError(Response response) {
        return parseResponseError(response, null);
    }

    /**
     * 解析服务器响应错误
     * @param response
     * @param e
     * @return
     */
    public static Business parseResponseError(Response response, Exception e) {
        Business bus = new Business();
        if(e!=null && e.getCause() instanceof JSONException) {
            String trace = Log.getStackTraceString(e.fillInStackTrace());
            bus.setErrorCode(ErrorCode.UNDEFINED);
            bus.setErrorMsg(trace);
            bus.setCode(BusinessCode.JSON_ERROR);
            bus.setMessage("JSON数据解析异常..");
        } else if(e!=null && e.getCause() instanceof UnknownHostException) {
            bus.setErrorCode(ErrorCode.NETWORK_ERROR);
            bus.setErrorMsg("UnknownHostException");
            bus.setCode(BusinessCode.RESPONSE_ERROR);
            bus.setMessage("移动网络异常.");
        } else if(e!=null && e.getCause() instanceof ConnectException) {
            String trace = Log.getStackTraceString(e);
            bus.setErrorCode(ErrorCode.NETWORK_ERROR);
            bus.setErrorMsg(trace);
            bus.setCode(BusinessCode.RESPONSE_ERROR);
            bus.setMessage("连接服务器失败异常.");
        } else if(e!=null && e.getCause() instanceof SocketTimeoutException) {
            String trace = Log.getStackTraceString(e.fillInStackTrace());
            bus.setErrorCode(ErrorCode.NETWORK_ERROR);
            bus.setErrorMsg(trace);
            bus.setCode(BusinessCode.RESPONSE_ERROR);
            bus.setMessage("网络连接超时异常.");
        } else if(e!=null && e instanceof RetrofitError && (e==null || ((RetrofitError) e).getBody()==null)) {
            String trace = (e!=null) ? e.toString():"";
            bus.setErrorCode(ErrorCode.NETWORK_ERROR);
            bus.setErrorMsg(trace);
            bus.setCode(BusinessCode.RESPONSE_ERROR);
            bus.setMessage("");
        } else if(e==null || e instanceof RetrofitError){
            //有HTTP异常情况|没有HTTP异常情况,都要解析Business数据
            int code        = BusinessCode.matchErrorCode(response);
            String message  = BusinessMsg.matchMessage(code);
            int errorCode   = (response!=null) ? response.getErrorCode() : BusinessCode.UNDEFINED;
            String errorMsg = (response!=null) ? response.getErrorMsg() : "";
            long requestId  = (response!=null) ? response.getRequestId() : 0;
            bus.setRequestId(requestId);
            bus.setErrorCode(errorCode);
            bus.setErrorMsg(errorMsg);
            bus.setCode(code);
            bus.setMessage(message);
        } else if(e!=null && e instanceof Exception) {
            String trace = Log.getStackTraceString(e.fillInStackTrace());
            bus.setErrorCode(ErrorCode.NETWORK_ERROR);
            bus.setErrorMsg(trace);
            bus.setCode(BusinessCode.RESPONSE_ERROR);
            bus.setMessage("");
        }
        Logger.oFile(bus.toString(), "aa");
        return bus;
    }

    @Override
    public String toString() {
        return "Business{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", requestId=" + requestId +
                '}';
    }
}


