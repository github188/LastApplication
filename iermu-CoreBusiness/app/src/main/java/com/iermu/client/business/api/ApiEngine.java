package com.iermu.client.business.api;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iermu.apiservice.ApiServiceModule;
import com.iermu.apiservice.RequestInterceptor;
import com.iermu.client.ErmuApplication;
import com.iermu.client.util.Logger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Api层引擎
 *
 * Created by wcy on 15/7/3.
 */
public class ApiEngine {

    private static ObjectGraph	mObjectGraph;

    public static void init(ErmuApplication app) {
        if(mObjectGraph == null) {
            synchronized (ApiEngine.class) {
                if(mObjectGraph == null) {
                    Application context = app.getContext();
                    mObjectGraph = ObjectGraph.create(new ApiMoudle(context));
                    mObjectGraph.plus(new ApiServiceModule());
                    mObjectGraph.inject(app);
                    mObjectGraph.injectStatics();
                    Logger.i("ApiEngine init end.");
                }
            }
        }
    }

    public static ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    @Module(
    includes = {
        ApiServiceModule.class
    }, injects = {
        ErmuApplication.class, Application.class
    },
    staticInjections = {
        MimeCamApi.class, CamSettingApi.class, PubCamApi.class, CamDeviceApi.class
        , AccountAuthApi.class, StreamMediaApi.class, CamShareApi.class, CloudPlatformApi.class
        , StatisticsApi.class
    })
    static class ApiMoudle {
        @SuppressWarnings("unused")
        private final Context appContext;

        public ApiMoudle(Context appContext) {
            this.appContext = appContext;
        }

//        @Provides
//        @Singleton
//        Interceptor provideResponseInterceptor() {
//            return new Interceptor() {
//                @Override
//                public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
//                    Request request = chain.request();
//                    com.squareup.okhttp.Response proceed = chain.proceed(request);
//                    ResponseBody body = proceed.body();
//                    return proceed;
//                }
//            };
//        }

        @Provides
        @Singleton
        RequestInterceptor provideRequestInterceptor() {
            return new RequestInterceptor(appContext);
        }

        @Provides
        @Singleton
        ErrorHandler provideErrorHandler() {
            return new ErrorHandler() {
                @Override
                public Throwable handleError(RetrofitError error) {
                    Response response = error.getResponse();
                    if (response != null) {
                        final int statusCode = response.getStatus();
                        if(statusCode == 110 || statusCode == 101) {
                            ErmuApplication.toast("110 退出登录.");
                            Logger.e("ApiEngine", "StatusCode:" + statusCode + " Reason:" + response.getReason()+"  登录失效.");
                        } else if(statusCode == 401) {
                            Logger.e("ApiEngine", "StatusCode:" + statusCode + " Reason:" + response.getReason());
                        } else if (statusCode != 200) {
                            Logger.e("ApiEngine", "StatusCode:"+statusCode+" Reason:"+response.getReason());
                        }
                    }
                    return error;
                }
            };
        }

        @Provides @Singleton
        Gson provideGson() {
            GsonBuilder builder = new GsonBuilder();
            return builder.create();
        }
    }

}
