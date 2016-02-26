package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * 我的摄像头API相关接口
 *
 * Created by wcy on 15/7/16.
 */
public interface MimeCamService {

    /**
     * 获取我的摄像机列表
     * @param method        函数名
     * @param deviceType    设备类型,默认为1, 预留字段
     * @param dataType      数据类型, 默认为'my'我的设备;'grant'授权设备,'sub'订阅设备,'all'所有以上
     * @param listType      列表类型, 默认为'all'所有设备, 'page'分页返回
     * @param page          页数, 默认为1, listType为page时有效
     * @param count         每页数据条数, 默认为10, listType为page时有效
     * @param accessToken   服务器AccessToken
     * @return
     */
    @POST(ApiRoute.v2_DEVICELIST)
    @FormUrlEncoded
    String getDeviceList(@Field("method") String method, @Field("device_type") int deviceType
            , @Field("data_type") String dataType, @Field("list_type") String listType
            , @Field("page") int page, @Field("count") int count, @Field("access_token") String accessToken);

    /**
     * 获取录像列表
     *
     * @param method
     * @param accessToken
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    @POST(ApiRoute.v2_RECORD_LIST)
    @FormUrlEncoded
    String getRecordList(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("st") int startTime, @Field("et") int endTime);

    /**
     * 获取缩略图列表
     *
     * @param method
     * @param accessToken
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    @POST(ApiRoute.v2_THUMBNAIL_LIST)
    @FormUrlEncoded
    String getThumbnailList(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("st") int startTime, @Field("et") int endTime);

}
