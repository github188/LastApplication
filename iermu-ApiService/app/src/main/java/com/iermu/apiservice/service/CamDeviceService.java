package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * 摄像机设备API相关接口
 *
 * Created by wcy on 15/7/23.
 */
public interface CamDeviceService {

    /**
     * 注册设备
     * @param method        请求方法名(register)
     * @param deviceId      设备ID
     * @param deviceType    设备类型，默认为1
     * @param connectType   平台类型，1:百度 2: 羚羊云
     * @param desc          设备名
     * @param accessToken   access token
     * @return
     */
    @POST(ApiRoute.v2_REGISTERDEV) //@Body RegisterDevBody body
    @FormUrlEncoded
    String registerDev(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("device_type") int deviceType, @Field("connect_type") int connectType
            , @Field("desc") String desc , @Field("access_token") String accessToken);

    /**
     * 观看摄像机(增加观看计数)
     * @param method
     * @param deviceId      设备ID
     * @param accessToken   access token
     * @return
     */
    @POST(ApiRoute.v2_CAMVIEW)
    @FormUrlEncoded
    String viewCam(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken);

}
