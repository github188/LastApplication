package com.iermu.client.business.api;

import com.iermu.apiservice.service.CamShareService;
import com.iermu.apiservice.service.CloudPlatformService;
import com.iermu.client.business.api.response.CloudListPresetResponse;
import com.iermu.client.business.api.response.CloudMoveResponse;
import com.iermu.client.business.api.response.CloudPlatResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.util.LoggerUtil;

import org.json.JSONException;

import javax.inject.Inject;

import retrofit.http.Field;

/**
 * Created by zhoushaopei on 15/10/19.
 */
public class CloudPlatformApi extends BaseHttpApi {

    @Inject
    static CloudPlatformService mApiService;

    public static CloudMoveResponse apiCloudMove(String deviceId, int x1, int y1, int x2, int y2, String accessToken) {
        CloudMoveResponse response;
        String method = "move";
        try {
            String str = mApiService.cloudMove(method, accessToken, deviceId, x1, y1, x2, y2);
            response = CloudMoveResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiCloudMove", e);
            response = CloudMoveResponse.parseResponseError(e);
        }
        return response;
    }

    public static Response apiCloudMovePreset(String deviceId, int preset, String accessToken) {
        Response response;
        String method = "move";
        try {
            String str = mApiService.cloudMovePreset(method, accessToken, deviceId, preset);
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiCloudMovePreset", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }


    public static Response apiCloudRotate(String deviceId, String rotate, String accessToken) {
        Response response;
        String method = "rotate";
        try {
            String str = mApiService.cloudRotate(method, accessToken, deviceId, rotate);
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiCloudRotate", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    public static Response apiAddPreset(String deviceId, int preset, String title, String accessToken) {
        Response response;
        String method = "addpreset";
        try {
            String str = mApiService.addPreset(method, accessToken, deviceId, preset, title);
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiAddPreset", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    public static Response apiDropPreset(String deviceId, int preset, String accessToken) {
        Response response;
        String method = "droppreset";
        try {
            String str = mApiService.dropPreset(method, accessToken, deviceId, preset);
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiDropPreset", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    public static CloudListPresetResponse apiListPreset(String deviceId, String accessToken) {
        CloudListPresetResponse response;
        String method = "listpreset";
        try {
            String str = mApiService.getListPreset(method, accessToken, deviceId);
            response = CloudListPresetResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiListPreset", e);
            response = CloudListPresetResponse.parseResponseError(e);
        }
        return response;
    }

    public static CloudPlatResponse apiGetPlatInfo(String deviceId, String accessToken) {
        CloudPlatResponse response;
        String method = "setting";
        String type   = "plat";
        try {
            String str = mApiService.getPlatInfo(method, accessToken, type, deviceId);
            response = CloudPlatResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetPlatInfo", e);
            response = CloudPlatResponse.parseResponseError(e);
        }
        return response;
    }



}

