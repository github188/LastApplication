package com.iermu.client.business.api;

/**
 * 百度账号关联的Auth验证相关接口
 *
 * Created by wcy on 15/7/19.
 */
public class BaiduAuthApi extends BaseHttpApi {

//    /**
//     * 获取AccessToken
//     * @param accessCode
//     * @param clientId
//     * @param clientSecret
//     * @param redirectUri
//     * @return
//     */
//    public static BaiduTokenResponse getAccessToken(String accessCode, String clientId, String clientSecret, String redirectUri) {
//        BaiduTokenResponse response;
//        try {
//            BaiduTokenRequest request = new BaiduTokenRequest(accessCode, clientId, clientSecret, redirectUri);
//            String post = get(ApiConfig.getPcsAccessToken(), request);
//            response = BaiduTokenResponse.parseResponse(post);
//        } catch (Exception e) {
//            LoggerUtil.e("getAccessToken", e);
//            response = BaiduTokenResponse.parseResponseError(e);
//        }
//        return response;
//    }


}
