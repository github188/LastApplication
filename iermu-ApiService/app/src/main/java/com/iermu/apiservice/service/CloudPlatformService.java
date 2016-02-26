package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * 云台控制API接口
 *
 * Created by zhoushaopei on 15/10/19.
 */
public interface CloudPlatformService {

    /**
     *  控制云台转动
     *
     * @param method
     * @param accessToken
     * @param deviceid      设备ID
     * @param x1            起点横坐标
     * @param y1            起点纵坐标
     * @param x2            终点横坐标
     * @param y2            终点纵坐标
     * @return
     */
    @POST(ApiRoute.V2_CLOUD_MOVE)
    @FormUrlEncoded
    String cloudMove(@Field("method") String method, @Field("access_token") String accessToken, @Field("deviceid") String deviceid
                                    , @Field("x1") int x1, @Field("y1") int y1, @Field("x2") int x2, @Field("y2") int y2);


    /**
     * 根据预置点控制云台转动
     * @param method
     * @param accessToken
     * @param deviceid
     * @param preset
     * @return
     */
    @POST(ApiRoute.V2_CLOUD_MOVE)
    @FormUrlEncoded
    String cloudMovePreset(@Field("method") String method, @Field("access_token") String accessToken, @Field("deviceid") String deviceid, @Field("preset") int preset);

    /**
     *  控制云台平扫
     *
     * @param method
     * @param accessToken
     * @param deviceid      设备ID
     * @param rotate        是否开启rotate, stop 关闭, auto 开启
     * @return
     */
    @POST(ApiRoute.V2_CLOUD_ROTATE)
    @FormUrlEncoded
    String cloudRotate(@Field("method") String method, @Field("access_token") String accessToken, @Field("deviceid") String deviceid, @Field("rotate") String rotate);

    /**
     *  添加云台预置点
     *
     * @param method
     * @param accessToken
     * @param deviceid      设备ID
     * @param preset        预置点序号
     * @param title         预置点名称
     * @return
     */
    @POST(ApiRoute.V2_CLOUD_ADDPRESET)
    @FormUrlEncoded
    String addPreset(@Field("method") String method, @Field("access_token") String accessToken, @Field("deviceid") String deviceid, @Field("preset") int preset, @Field("title") String title);

    /**
     * 删除云台预置点
     *
     * @param method
     * @param accessToken
     * @param deviceid      设备ID
     * @param preset        预置点序号
     * @return
     */
    @POST(ApiRoute.V2_CLOUD_DROPPRESET)
    @FormUrlEncoded
    String dropPreset(@Field("method") String method, @Field("access_token") String accessToken, @Field("deviceid") String deviceid, @Field("preset") int preset);

    /**
     * 获取云台预置点列表
     *
     * @param method
     * @param accessToken
     * @param deviceid      设备ID
     * @return
     */
    @POST(ApiRoute.V2_CLOUD_LISTPRESET)
    @FormUrlEncoded
    String getListPreset(@Field("method") String method, @Field("access_token") String accessToken, @Field("deviceid") String deviceid);

    /**
     *  获取云台信息
     *
     * @param method
     * @param accessToken
     * @param deviceId
     * @return
     */
    @POST(ApiRoute.V2_CLOUD_PLAT)
    @FormUrlEncoded
    String getPlatInfo(@Field("method") String method, @Field("access_token") String accessToken, @Field("type") String type, @Field("deviceid") String deviceId);

}
