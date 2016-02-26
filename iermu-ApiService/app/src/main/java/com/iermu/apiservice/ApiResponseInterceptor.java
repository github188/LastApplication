package com.iermu.apiservice;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Api Response拦截器
 *
 * Created by wcy on 15/9/10.
 */
public class ApiResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        return null;
    }
}
