package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * 账号关联的Auth验证相关接口
 *
 * Created by wcy on 15/7/23.
 */
public interface AccountAuthService {

    /**
     * 注册用户
     * @param method
     * @param username  用户名
     * @param password  密码
     * @param email     邮箱
     * @param clientId  AK
     * @param sign      用户名＋SK
     * @param expire    时间戳
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_REGISTER)
    String register(@Field("method") String method, @Field("username") String username, @Field("password") String password, @Field("email") String email
            ,  @Field("client_id") String clientId, @Field("sign") String sign, @Field("expire") int expire, @Field("lang") String language);

    /**
     * 使用用户名和密码获取的token
     * @param granType
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @param scope
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_TOKEN)
    String getRegisterToken(@Field("grant_type") String granType, @Field("username") String username, @Field("password") String password
            ,  @Field("client_id") String clientId, @Field("client_secret") String clientSecret, @Field("scope") String scope);


    /**
     * 获取AccessToken
     * @param grantType     固定为“authorization_code”
     * @param code          Authorization Code
     * @param clientId      应用的 API Key
     * @param redirectUri   该值必须与获取 Authorization Code 时传递的 “redirect_uri”保持一致
     * @return
     */
    @GET(ApiRoute.v2_TOKEN)
    String getToken(@Query("grant_type") String grantType, @Query("code") String code
            , @Query("client_id") String clientId, @Query("redirect_uri") String redirectUri);

    /**
     * 获取用户信息、绑定平台信息
     * @param method
     * @param accessToken
     * @param connect       是否获取平台信息
     * @return
     */
    @GET(ApiRoute.v2_USERINFO)
    String getUserInfo(@Query("method") String method, @Query("access_token") String accessToken, @Query("connect") int connect);

    /**
     * 修改用户名
     * @param method
     * @param userName
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_USERINFO)
    String updateUserName(@Field("method") String method, @Field("username") String userName, @Field("access_token") String access_token);

    /**
     * 修改账号密码
     * @param method
     * @param uid
     * @param password
     * @param sign
     * @param expire
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_USERINFO)
    String updatePassword(@Field("method") String method, @Field("uid") String uid, @Field("oldpassword") String old_password, @Field("newpassword") String password
                        , @Field("sign") String sign, @Field("expire") int expire, @Field("access_token") String access_token);

    /**
     * 完善用户资料
     * @param method
     * @param username
     * @param email
     * @param password
     * @param sign
     * @param expire
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_USERINFO)
    String completeUserInfo(@Field("method") String method, @Field("username") String username, @Field("email") String email, @Field("password") String password
                        , @Field("sign") String sign, @Field("expire") int expire, @Field("access_token") String access_token);

    /**
     * 反馈信息
     * @param method
     * @param clientId
     * @param opinion
     * @param contact
     * @param accessToken
     * @param telmodel
     * @param version
     * @return
     */
    @GET(ApiRoute.v2_FEEDBACK)
    String feedBack(@Query("method") String method, @Query("client_id") String clientId, @Query("opinion") String opinion, @Query("contact") String contact
            , @Query("access_token") String accessToken, @Query("telmodel") String telmodel, @Query("version") String version);

    /**
     * 更新平台信息
     * @param method
     * @param connectType   平台类型，2羚羊云
     * @param access_token
     * @param param         connect信息，JSON串，如羚羊云更新user_token需传 {"user_token":"11111111111111"}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATECONNECT)
    String updateConnect(@Field("method") String method, @Field("connect_type") int connectType
                         ,@Field("access_token") String access_token, @Field("param") String param);

    /**
     * 注销登录
     *
     * @param method
     * @param accessToken
     * @return
     */
    @GET(ApiRoute.v2_LOGOUT)
    String logout(@Query("method") String method, @Query("access_token") String accessToken);

    /**
     * 获取活动海报
     *
     * @param method
     * @param clientId  APP KEY
     * @param width     屏幕宽度
     * @param height    屏幕高度
     * @return
     */
    @GET(ApiRoute.V2_CLIENT)
    String newPoster(@Query("method") String method, @Query("client_id") String clientId, @Query("width") int width, @Query("height") int height, @Query("lang") String language);
}
