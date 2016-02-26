package com.iermu.client.business.api;

import com.iermu.apiservice.service.MimeCamService;
import com.iermu.client.business.api.response.CamRecordListResponse;
import com.iermu.client.business.api.response.CamThumbnailListResponse;
import com.iermu.client.business.api.response.MimeCamListResponse;
import com.iermu.client.util.LoggerUtil;

import javax.inject.Inject;

/**
 * 我的摄像头相关接口
 * <p/>
 * Created by wcy on 15/6/26.
 */
public class MimeCamApi extends BaseHttpApi {

    @Inject
    static MimeCamService mApiService;

    /**
     * 获取我的摄像机列表
     *
     * @param devType 设备类型
     * @param page    分页数
     * @return
     */
    public static MimeCamListResponse apiDeviceList(int devType, int page, String accessToken) {
        MimeCamListResponse response;
        String method = "list";
        String dataType = "all";
        String listType = "page";
        try {
            String res = mApiService.getDeviceList(method, devType, dataType, listType, page, 100, accessToken);
            response = MimeCamListResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiDeviceList", e);
            response = MimeCamListResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取我的摄像机列表
     *
     * @param devType 设备类型
     * @param page    分页数
     * @return
     */
    public static MimeCamListResponse apiDeviceOfTypeMyList(int devType, int page, String accessToken) {
        MimeCamListResponse response;
        String method = "list";
        String dataType = "my";
        String listType = "page";
        try {
            String res = mApiService.getDeviceList(method, devType, dataType, listType, page, 15, accessToken);
            response = MimeCamListResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiDeviceList", e);
            response = MimeCamListResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取授权的摄像机列表
     *
     * @param devType     设备类型
     * @param accessToken
     * @return
     */
    public static MimeCamListResponse apiGrantDeviceList(int devType, String accessToken) {
        MimeCamListResponse response;
        String method = "list";
        String dataType = "grant";
        String listType = "all";
        try {
            String res = mApiService.getDeviceList(method, devType, dataType, listType, 0, 0, accessToken);
            response = MimeCamListResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiGrantDeviceList", e);
            response = MimeCamListResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取我的摄像机云服务状态
     *
     * @param devType 设备类型
     * @param page    分页数
     * @return
     */
    public static MimeCamListResponse apiDevCloud(int devType, int page, String accessToken) {
        MimeCamListResponse response;
        String method = "list";
        String dataType = "my";
        String listType = "page";
        try {
            String res = mApiService.getDeviceList(method, devType, dataType, listType, page, 15, accessToken);
            response = MimeCamListResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiDevCloud", e);
            response = MimeCamListResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取百度录像列表
     *
     * @param deviceId
     * @param accessToken
     * @param startTime
     * @param endTime
     * @return
     */
    public static CamRecordListResponse apiGetBaiduRecordList(String deviceId, String accessToken, int startTime, int endTime) {
        CamRecordListResponse response;
        String method = "playlist";
        try {
            String str = mApiService.getRecordList(method, deviceId, accessToken, startTime, endTime);
            response = CamRecordListResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetRecordList", e);
            response = CamRecordListResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取羚羊云录像列表
     *
     * @param deviceId
     * @param accessToken
     * @param startTime
     * @param endTime
     * @return
     */
    public static CamRecordListResponse apiGetLyyRecordList(String deviceId, String accessToken, int startTime, int endTime) {
        CamRecordListResponse response;
        String method = "playlist";
        try {
            String str = mApiService.getRecordList(method, deviceId, accessToken, startTime, endTime);
            response = CamRecordListResponse.parseLyyResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetRecordList", e);
            response = CamRecordListResponse.parseResponseError(e);
        }
        return response;
    }


    /**
     * 获取缩略图列表
     *
     * @param deviceId
     * @param accessToken
     * @param startTime
     * @param endTime
     * @return
     */
    public static CamThumbnailListResponse apiGetThumbnailList(String deviceId, String accessToken, int startTime, int endTime, boolean isLYY) {
        CamThumbnailListResponse response;
        String method = "thumbnail";
        try {
            String str = mApiService.getThumbnailList(method, deviceId, accessToken, startTime, endTime);
            if (isLYY) {
                response = CamThumbnailListResponse.parseLYYResponse(str);
            } else {
                response = CamThumbnailListResponse.parseResponse(str);
            }
        } catch (Exception e) {
            LoggerUtil.e("apiGetThumbnailList", e);
            response = CamThumbnailListResponse.parseResponseError(e);
        }
        return response;
    }


}
