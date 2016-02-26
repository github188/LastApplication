package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by zhoushaopei on 16/1/19.
 */
public interface UserCenterService {

    @FormUrlEncoded
    @GET(ApiRoute.V2_CLIP)
    String getPhotoClip(@Query("method") String method , @Query("path") String path
            , @Query("access_token") String accessToken);


}
