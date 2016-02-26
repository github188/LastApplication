package com.iermu.client.business.api;

import com.iermu.apiservice.service.CamShareService;
import com.iermu.client.business.api.response.CreateShareResponse;
import com.iermu.client.business.api.response.GetGrantCodeResponse;
import com.iermu.client.business.api.response.GrantShareResponse;
import com.iermu.client.business.api.response.GrantUsersResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.util.LoggerUtil;

import javax.inject.Inject;

/**
 * 摄像机分享、授权Api接口
 * <p>
 * Created by zhoushaopei on 15/7/13.
 */
public class CamShareApi extends BaseHttpApi {

    @Inject
    static CamShareService mApiService;

    /**
     * 获取授权用户列表
     *
     * @param deviceId
     * @param accessToken
     * @return
     */
    public static GrantUsersResponse apiGrantUsers(String deviceId, String accessToken) {
        GrantUsersResponse response;
        String method = "listgrantuser";
        try {
            String str = mApiService.getGrantUsers(method, deviceId, accessToken);
            response = GrantUsersResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGrantUsers", e);
            response = GrantUsersResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 创建分享链接
     *
     * @param deviceId
     * @param title
     * @param introduce
     * @param shareType
     * @param accessToken
     * @return
     */
    public static CreateShareResponse createShare(String deviceId, String title, String introduce, int shareType, String accessToken) {
        CreateShareResponse response;
        String method = "createshare";
        try {
            String str = mApiService.createShare(method, accessToken, deviceId, title, introduce, shareType);
            response = CreateShareResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiCreateShare", e);
            response = CreateShareResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 取消分享链接
     *
     * @param accessToken
     * @param deviceId
     * @return
     */
   public static Response cancleShare(String accessToken, String deviceId) {
       Response response;
       String method = "cancelshare";
       try {
           String str = mApiService.cancleShare(method, accessToken, deviceId);
           response = Response.parseResponse(str);
       } catch (Exception e) {
           LoggerUtil.e("apiDropUser", e);
           response = Response.parseResponseError(e);
       }
       return response;
   }

    /**
     * 获取设备授权码
     *
     * @param deviceId      设备ID
     * @param accessToken
     * @return
     */
    public static GetGrantCodeResponse apiGetGrantCode(String deviceId, String accessToken) {
        GetGrantCodeResponse response;
        String method = "grantcode";
        try {
            String str = mApiService.getGrantCode(method, deviceId, accessToken);
            response = GetGrantCodeResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetGrantCode", e);
            response = GetGrantCodeResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 把云摄像头授权给其他用户
     *
     * @param code
     * @return
     */
    public static GrantShareResponse apiGrantShare(String code, String accessToken) {
        GrantShareResponse response;
        String method = "grant";
        String authCode = "5";
        try {
            String str = mApiService.grantShare(method, accessToken, authCode, code);
            response = GrantShareResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetGrantCode", e);
            response = GrantShareResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 删除设备授权用户信息
     *
     * @param deviceId
     * @param uk
     * @param accessToken
     * @return
     */
   public static Response apiDropUser(String deviceId, String uk, String accessToken) {
       Response response;
       String method = "dropgrantuser";
       try {
           String str = mApiService.dropGrantUser(method, accessToken, deviceId, uk);
           response = Response.parseResponse(str);
       } catch (Exception e) {
           LoggerUtil.e("apiDropUser", e);
           response = Response.parseResponseError(e);
       }
       return response;
   }

    /**
     * 删除某个用户名下被授权的云摄像头
     *
     * @param deviceId
     * @param accessToken
     * @return
     */
   public static Response apiDropGrantDevice(String deviceId, String accessToken) {
       Response response;
       String method = "dropgrantdevice";
       try {
           String str = mApiService.dropGrantDevice(method, accessToken, deviceId);
           response = Response.parseResponse(str);
       } catch (Exception e) {
           LoggerUtil.e("dropgrantdevice", e);
           response = Response.parseResponseError(e);
       }
       return response;
   }


}
