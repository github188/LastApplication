package com.iermu.apiservice;

import com.google.gson.Gson;
import com.iermu.apiservice.service.AccountAuthService;
import com.iermu.apiservice.service.CamDeviceService;
import com.iermu.apiservice.service.CamSettingService;
import com.iermu.apiservice.service.CamShareService;
import com.iermu.apiservice.service.CloudPlatformService;
import com.iermu.apiservice.service.MimeCamService;
import com.iermu.apiservice.service.PubCamService;
import com.iermu.apiservice.service.StatisticsService;
import com.iermu.apiservice.service.StreamMediaService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;

/**
 * Api接口模块
 *
 * Created by wcy on 15/7/1.
 */
@Module(library = true, complete = false)
public class ApiServiceModule {

    @Provides
    @Singleton @ApiRestAdapter
    RestAdapter provideRestAdapter(Gson gson, RequestInterceptor requestInterceptor
            //, Interceptor responseInterceptor
            , ErrorHandler errorHandler) {
        //ApiApacheClient client = new ApiApacheClient();
        ApiOkClient client = new ApiOkClient();
        //client.addInterceptor(responseInterceptor);
        return new RestAdapter.Builder()
                .setEndpoint(ApiRoute.IP)
                .setClient(client)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                //.setConverter(new GsonConverter(gson, ApiConfig.CHARSET))
                .setConverter(new StringConverter(gson, ApiRoute.CHARSET))
                .setRequestInterceptor(requestInterceptor)
                .setErrorHandler(errorHandler)
                .build();
    }

    @Provides @Singleton
    CamSettingService provideCamSettingService(@ApiRestAdapter RestAdapter adapter) {
        return adapter.create(CamSettingService.class);
    }

    @Provides @Singleton
    MimeCamService provideMimeCamService(@ApiRestAdapter RestAdapter adapter) {
        return adapter.create(MimeCamService.class);
    }

    @Provides @Singleton
    PubCamService providePubCamService(@ApiRestAdapter RestAdapter adapter) {
        return adapter.create(PubCamService.class);
    }

    @Provides @Singleton
    CamDeviceService provideCamDeviceService(@ApiRestAdapter RestAdapter adapter) {
        return adapter.create(CamDeviceService.class);
    }

    @Provides @Singleton
    AccountAuthService provideAccountAuthService(@ApiRestAdapter RestAdapter adapter) {
        return adapter.create(AccountAuthService.class);
    }

    @Provides @Singleton
    StreamMediaService provideStreamMediaService(@ApiRestAdapter RestAdapter adapter) {
        return adapter.create(StreamMediaService.class);
    }

    @Provides @Singleton
    CamShareService provideCamShareService(@ApiRestAdapter RestAdapter adapter) {
        return adapter.create(CamShareService.class);
    }

    @Provides @Singleton
    CloudPlatformService provideCloudPlatformService(@ApiRestAdapter RestAdapter adapter) {
        return adapter.create(CloudPlatformService.class);
    }

    @Provides @Singleton
    StatisticsService provideStatisticsService(@ApiRestAdapter RestAdapter adapter) {
        return adapter.create(StatisticsService.class);
    }
}
