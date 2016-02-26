package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * 公开摄像头API相关接口
 * <p/>
 * Created by wcy on 15/7/15.
 */
public interface PubCamService {

    /**
     * 获取摄像机信息
     *
     * @param method
     * @param deviceId    设备ID
     * @param accessToken AccessToken
     * @param shareid     公开分享ID
     * @param uk          用户ID(百度)
     * @return
     */
    @POST(ApiRoute.v2_DEVMETA)
    @FormUrlEncoded
    String getCamMeta(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("shareid") String shareid
            , @Field("uk") String uk);

    /**
     * 获取公共频道列表
     *
     * @param method
     * @param sign
     * @param expire
     * @param page
     * @param count
     * @param orderby
     * @return
     */
    @POST(ApiRoute.v2_PUBLIST)
    @FormUrlEncoded
    String getCamList(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("sign") String sign, @Field("expire") long expire, @Field("page") int page
            , @Field("count") int count, @Field("orderby") String orderby);

    /**
     * 获取公共评论列表
     *
     * @param method
     * @param page
     * @param count
     * @return
     */
    @POST(ApiRoute.v2_COMMENTLIST)
    @FormUrlEncoded
    String getCamCommentList(@Field("method") String method, @Field("shareid") String shareId
            , @Field("uk") String uk, @Field("page") int page, @Field("count") int count);


    /**
     * 发表公共评论
     *
     * @param method
     * @param deviceid
     * @param accessToken
     * @param comment
     * @param parentCId
     * @return
     */
    @POST(ApiRoute.v2_COMMENT)
    @FormUrlEncoded
    String sendComment(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("deviceid") String deviceid, @Field("comment") String comment
            , @Field("parent_cid") int parentCId);

    /**
     * 点赞
     *
     * @param method
     * @param accessToken
     * @param deviceId
     * @return
     */
    @POST(ApiRoute.v2_APPROVE)
    @FormUrlEncoded
    String favour(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("deviceid") String deviceId);

    /**
     * 收藏
     *
     * @param method
     * @param accessToken
     * @param shareId
     * @param uk
     * @return
     */
    @POST(ApiRoute.v2_SUBSCRIBE)
    @FormUrlEncoded
    String store(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("shareid") String shareId, @Field("uk") String uk);

    /**
     * 取消收藏
     *
     * @param method
     * @param accessToken
     * @param shareId
     * @param uk
     * @return
     */
    @POST(ApiRoute.v2_UNSUBSCRIBE)
    @FormUrlEncoded
    String unStore(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("shareid") String shareId, @Field("uk") String uk);
}
