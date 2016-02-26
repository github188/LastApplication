package com.iermu.client.business.api;

import com.iermu.apiservice.service.CamDeviceService;
import com.iermu.client.business.api.response.RegisterDevResponse;
import com.iermu.client.business.api.response.ViewCamResponse;
import com.iermu.client.util.LoggerUtil;

import javax.inject.Inject;

/**
 * 摄像机设备相关接口
 */
public class CamDeviceApi extends BaseHttpApi {

    @Inject static CamDeviceService mApiService;

    /**
     * 注册设备
     * @param deviceId      设备ID
     * @param deviceType    设备类型
     * @param connectType   设备连接平台类型
     * @param desc          设备名称
     * @param accessToken   accessToken
     * @return
     */
    public static RegisterDevResponse registerDevice(String deviceId, int deviceType, int connectType
            , String desc, String accessToken) {
        RegisterDevResponse response;
        String method = "register";
        try {
            String str = mApiService.registerDev(method, deviceId, deviceType, connectType
                    , desc, accessToken);
            response = RegisterDevResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("registerDevice", e);
            response = RegisterDevResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 观看摄像机(统计计数)
     * @param deviceId      设备ID
     * @param accessToken
     * @return
     */
    public static ViewCamResponse viewCam(String deviceId, String accessToken) {
        ViewCamResponse response;
        String method = "view";
        try {
            String str = mApiService.viewCam(method, deviceId, accessToken);
            response = ViewCamResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("viewCam", e);
            response = ViewCamResponse.parseResponseError(e);
        }
        return response;
    }

}
