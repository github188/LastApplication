package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * 流媒体API相关接口
 *
 * Created by wcy on 15/7/29.
 */
public interface StreamMediaService {

    /**
     * 获取直播信息
     * @param method        函数名
     * @param deviceId      设备ID
     * @param accessToken   AccessToken
     * @param shareid       公开分享ID
     * @param uk            用户ID(百度)
     * @return
     */
    @POST(ApiRoute.v2_LIVEPLAY)
    @FormUrlEncoded
    String getLivePlay(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("shareid") String shareid
            , @Field("uk") String uk);


    /**
     * 录像开始时间校准
     *
     * @param method
     * @param deviceId
     * @param accessToken
     * @param time
     * @return
     */
    @POST(ApiRoute.v2_VODSEEK)
    @FormUrlEncoded
    String recordVodSeek(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("time") Integer time);
}
