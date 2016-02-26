package com.iermu.client.business.api;

import com.iermu.apiservice.service.PubCamService;
import com.iermu.client.business.api.response.CamMetaResponse;
import com.iermu.client.business.api.response.CommentListResponse;
import com.iermu.client.business.api.response.CommentSendResponse;
import com.iermu.client.business.api.response.FavourResponse;
import com.iermu.client.business.api.response.PubCamListResponse;
import com.iermu.client.business.api.response.StoreResponse;
import com.iermu.client.util.LoggerUtil;

import javax.inject.Inject;

/**
 * 摄像机分享、授权Api接口
 * <p/>
 * Created by wcy on 15/7/22.
 */
public class PubCamApi extends BaseHttpApi {

    @Inject
    static PubCamService mApiService;

    /**
     * 获取摄像机直播信息
     *
     * @param deviceId    设备ID
     * @param accessToken accessToken
     * @param shareId     公开分享ID
     * @param uk          用户ID
     * @retrn
     */
    public static CamMetaResponse apiCamMeta(String deviceId, String accessToken, String shareId, String uk) {
        CamMetaResponse response;
        try {
            String method = "meta";
            String res = mApiService.getCamMeta(method, deviceId, accessToken, shareId, uk);
            response = CamMetaResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiCamMeta", e);
            response = CamMetaResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取公共摄像机列表
     *
     * @param sign
     * @param expire
     * @param page
     * @param orderby
     * @return
     */
    public static PubCamListResponse getPubCamList(String accessToken, String sign, long expire, int page, String orderby) {
        PubCamListResponse response;
        try {
            String method = "listshare";
            int num = 10;
            String res = mApiService.getCamList(method, accessToken, sign, expire, page, num, orderby);
            response = PubCamListResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiPubCamList", e);
            response = PubCamListResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取公共摄像机评论列表
     *
     * @param page
     * @return
     */
    public static CommentListResponse getCommentList(String shareId, String uk, int page, int count) {
        CommentListResponse response;
        try {
            String method = "listcomment";
            String res = mApiService.getCamCommentList(method, shareId, uk, page, count);
            response = CommentListResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiPubCamList", e);
            response = CommentListResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 发表评论
     *
     * @param accessToken
     * @param deviceId
     * @param comment
     * @param parentId
     * @return
     */
    public static CommentSendResponse sendComment(String accessToken, String deviceId, String comment, int parentId) {
        CommentSendResponse response;
        try {
            String method = "comment";
            String res = mApiService.sendComment(method, accessToken, deviceId, comment, parentId);
            response = CommentSendResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiSendComment", e);
            response = CommentSendResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 点赞
     *
     * @param accessToken
     * @param deviceId
     * @return
     */
    public static FavourResponse favour(String accessToken, String deviceId) {
        FavourResponse response;
        try {
            String method = "approve";
            String res = mApiService.favour(method, accessToken, deviceId);
            response = FavourResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiFavour", e);
            response = FavourResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 收藏
     *
     * @param accessToken
     * @param shareId
     * @param uk
     * @return
     */
    public static StoreResponse store(String accessToken, String shareId, String uk) {
        StoreResponse response;
        try {
            String method = "subscribe";
            String res = mApiService.store(method, accessToken, shareId, uk);
            response = StoreResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiFavour", e);
            response = StoreResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 取消收藏
     *
     * @param accessToken
     * @param shareId
     * @param uk
     * @return
     */
    public static StoreResponse unStore(String accessToken, String shareId, String uk) {
        StoreResponse response;
        try {
            String method = "unsubscribe";
            String res = mApiService.store(method, accessToken, shareId, uk);
            response = StoreResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiFavour", e);
            response = StoreResponse.parseResponseError(e);
        }
        return response;
    }
}
