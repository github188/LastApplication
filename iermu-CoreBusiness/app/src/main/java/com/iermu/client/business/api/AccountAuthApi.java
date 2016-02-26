package com.iermu.client.business.api;

import com.iermu.apiservice.service.AccountAuthService;
import com.iermu.client.ErmuApplication;
import com.iermu.client.business.api.response.ClientResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.business.api.response.TokenResponse;
import com.iermu.client.business.api.response.UserInfoResponse;
import com.iermu.client.config.AppConfig;
import com.iermu.client.util.DeviceUtil;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.Logger;
import com.iermu.client.util.LoggerUtil;
import com.iermu.client.util.MD5;
import com.iermu.client.util.StringUtil;

import org.json.JSONException;

import java.util.Date;

import javax.inject.Inject;

/**
 * 账号关联的Auth验证相关接口
 *
 * Created by wcy on 15/7/23.
 */
public class AccountAuthApi extends BaseHttpApi {

    @Inject static AccountAuthService mApiService;

    /**
     * 注册用户
     * @param username
     * @param password
     * @param email
     * @return
     */
    public static UserInfoResponse register(String username, String password, String email) {
        UserInfoResponse response;
        String method = "register";
        String clientId = AppConfig.AK;
        int expire = (int) (new Date().getTime()/1000+AppConfig.REGISTER_EXPIRE);
        try {
//            username = new String(username.getBytes(), "UTF-8");
            String sign = MD5.GetMD5Code(username + AppConfig.SK + expire);
            String language = LanguageUtil.getLanguage();
            String res = mApiService.register(method, username, password, email, clientId, sign, expire, language);
            response = UserInfoResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("register", e);
            response = UserInfoResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 根据用户名获取token
     * @param userName
     * @param password
     * @return
     */
    public static TokenResponse getRegisterToken(String userName, String password) {
        TokenResponse response;
        String grantType        = "password";
        String clientId = AppConfig.AK;
        String clientSecret = AppConfig.SK;
        String scope = "basic";
        try {
            String res = mApiService.getRegisterToken(grantType, userName, password, clientId, clientSecret, scope);
            response = TokenResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("getRegisterToken", e);
            response = TokenResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取AccessToken
     * @param code          授权码(Authorization Code)
     * @param clientId      应用的 API Key
     * @param redirectUri   该值必须与获取 Authorization Code 时传递的 “redirect_uri”保持一致
     */
    public static TokenResponse getToken(String code, String clientId, String redirectUri) {
        TokenResponse response  = new TokenResponse();
        String grantType        = "authorization_code";
        try {
            Logger.i("--currentTiem--" + System.currentTimeMillis() + "--code--" +code) ;
            String res = mApiService.getToken(grantType, code, clientId, redirectUri);
            Logger.i("--currentTiemOne--" + System.currentTimeMillis() + "--code--" +code) ;
            response = TokenResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("getToken", e);
            Logger.i("--currentTiemTwo--" + System.currentTimeMillis()) ;
            response = TokenResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取用户信息
     * @param accessToken
     * @return
     */
    public static UserInfoResponse getUserInfo(String accessToken) {
        UserInfoResponse response;
        String method = "info";
        int connect = 1;    //获取平台信息
        try {
            String res = mApiService.getUserInfo(method, accessToken, connect);
            response = UserInfoResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("getUserInfo", e);
            response = UserInfoResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 信息反馈
     * @param opinion       反馈意见
     * @param contact       联系方式
     * @param accessToken
     * @return
     */
    public static Response feedBack(String opinion, String contact, String accessToken) {
        Response response;
        String method = "feedback";
        String clientId = AppConfig.AK;
        String telmodel = DeviceUtil.getPhoneType();
        String version = ErmuApplication.getVersionName();
        try {
            String str = mApiService.feedBack(method, clientId, opinion, contact, accessToken, telmodel, version);
            response = Response.parseResponse(str);
        } catch (JSONException e) {
            LoggerUtil.e("feedBack", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 修改用户名
     * @param userName      用户名
     * @param accessToken
     * @return
     */
    public static Response updateUserName(String userName, String accessToken) {
        Response response;
        String method = "update";
        try {
            String res = mApiService.updateUserName(method, userName, accessToken);
            response = Response.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("updateUserName", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 修改密码
     * @param uid           用户ID
     * @param password      用户名密码
     * @param accessToken
     * @return
     */
    public static Response updatePassword(String uid, String oldPassword, String password, String accessToken) {
        Response response;
        String method = "changepwd";
        int expire = (int) (new Date().getTime()/1000+AppConfig.REGISTER_EXPIRE);
        String sing = StringUtil.string2MD5(uid + oldPassword + password + AppConfig.SK + expire);
        try {
            String str = mApiService.updatePassword(method, uid, oldPassword, password, sing, expire, accessToken);
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("updatePassword", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 完善用户资料
     * @param username      用户名
     * @param email         邮箱
     * @param password      密码
     * @param accessToken
     * @return
     */
    public static Response completeUserInfo(String username, String email, String password, String accessToken) {
        Response response;
        String method = "complete";
        int expire = (int) (new Date().getTime()/1000+AppConfig.REGISTER_EXPIRE);
        String sign = MD5.GetMD5Code(username + email + password + AppConfig.SK + expire);
        try {
            String str = mApiService.completeUserInfo(method, username, email, password, sign, expire, accessToken);
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("completeUserInfo", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 注销登录
     *
     * @param accessToken
     * @return
     */
    public static Response logout(String accessToken) {
        Response response;
        String method = "logout";
        try {
            String res = mApiService.logout(method, accessToken);
            response = Response.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("logout", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取活动海报
     * @param width
     * @param height
     * @return
     */
    public static ClientResponse newPoster(int width, int height) {
        ClientResponse response;
        String method = "getnewposter";
        String ak = AppConfig.AK;
        String language = LanguageUtil.getLanguage();
        try {
             String str = mApiService.newPoster(method, ak, width, height, language);
            response = ClientResponse.parseResponse(str);
            Logger.i("newposter:" + str);
        } catch (Exception e) {
            LoggerUtil.e("newPoster", e);
            response = ClientResponse.parseResponseError(e);
        }
        return response;
    }
}
