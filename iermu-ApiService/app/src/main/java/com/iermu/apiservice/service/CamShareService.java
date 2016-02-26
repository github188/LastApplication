package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by zhoushaopei on 15/7/13.
 */
public interface CamShareService {


    /**
     * 获取授权用户列表
     * @param method
     * @param deviceId      设备ID
     * @param accessToken
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_GRANTUSERS)
    String getGrantUsers(@Field("method") String method, @Field("deviceid") String deviceId, @Field("access_token") String accessToken);

    /**
     * 获取设备授权吗
     *
     * @param method
     * @param deviceId      设备ID
     * @param accessToken
     * @return
     */
    @POST(ApiRoute.v2_CREATE_CODE)
    @FormUrlEncoded
    String getGrantCode(@Field("method") String method, @Field("deviceid") String deviceId, @Field("access_token") String accessToken);

    /**
     * 把摄像头授权给其他用户
     *
     * @param method
     * @param accessToken
     * @param authCode      授权的权限：5
     * @param code          授权码，通过grantcode获取的code
     * @return
     */
    @POST(ApiRoute.v2_GRANT_SHARE)
    @FormUrlEncoded
    String grantShare(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("auth_code") String authCode, @Field("code") String code);

    /**
     * 删除设备授权用户信息
     *
     * @param method
     * @param accessToken
     * @param deviceid
     * @param uk
     * @return
     */
    @POST(ApiRoute.v2_DROP_GRANTUSER)
    @FormUrlEncoded
    String dropGrantUser(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("deviceid") String deviceid, @Field("uk") String uk);

    /**
     * 创建分享接口
     *
     * @param method
     * @param accessToken
     * @param deviceid      设备ID
     * @param title         分享名称
     * @param intro         分享简介
     * @param shareType     分享类型:1公共直播分享 2私密直播分享 3公开直播与录像分享 4私密直播与录像分享
     * @return
     */
    @POST(ApiRoute.v2_CREATE_SHARE)
    @FormUrlEncoded
    String createShare(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("deviceid") String deviceid, @Field("title") String title
            , @Field("intro") String intro, @Field("share") int shareType);

    /**
     * 取消分享接口
     *
     * @param method
     * @param accessToken
     * @param deviceid      设备ID
     * @return
     */
    @POST(ApiRoute.v2_CANCLE_SHARE)
    @FormUrlEncoded
    String cancleShare(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("deviceid") String deviceid);

    /**
     * 删除某个用户名下被授权的云摄像头
     *
     * @param method
     * @param accessToken
     * @param deviceid      设备ID
     * @return
     */
    @POST(ApiRoute.v2_CANCLE_SHARE)
    @FormUrlEncoded
    String dropGrantDevice(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("deviceid") String deviceid);
}
