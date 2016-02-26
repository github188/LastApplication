package com.iermu.apiservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by wcy on 15/7/1.
 */
public class RequestInterceptor implements retrofit.RequestInterceptor {

    private final CookieManager cookieManager;
    private final Context appContext;
    private final String userAgent;

    @Inject
    public RequestInterceptor(Context appContext) {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        this.appContext = appContext;
        this.userAgent = getUserAgent();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void intercept(RequestFacade requestFacade) {
        for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            Date expiration = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
            String expires = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
                    .format(expiration);
            String cookieValue = cookie.getName() + "=" + cookie.getValue() + ";"
                    + "path="	 + cookie.getPath()	+ ";"
                    + "domain="	 + cookie.getDomain() + ";"
                    + "expires=" + expires;
            requestFacade.addHeader("Cookie", cookieValue);
        }
        requestFacade.addHeader("user-agent", userAgent);
    }

    private String getUserAgent() {
        StringBuilder sb = new StringBuilder();
        sb.append(ApiRoute.API_VERSION)
          .append("/")
          .append(getAppVersionName(appContext))
          .append(" ")
          .append("(").append(android.os.Build.MODEL).append("; ")
                      .append(android.os.Build.VERSION.SDK).append(" ")
                      .append(android.os.Build.VERSION.RELEASE)
          .append(")");
        return sb.toString();
    }

    private String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
