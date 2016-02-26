package com.iermu.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.iermu.client.config.ApiConfig;
import com.iermu.client.util.Logger;

/**
 * Created by guixiaomei on 15/8/27.
 */
public class TestLoginActivity extends BaseActivity {

    private WebView mWebView;

    /**
     * 启动TestLoginActivity
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TestLoginActivity.class);
        context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = new WebView(this);
        setContentView(mWebView);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSavePassword(false);
        settings.setSaveFormData(false);
        settings.setUseWideViewPort(true);      //设置加载进来的页面自适应手机屏幕
        settings.setLoadWithOverviewMode(true);
        //settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        try {
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(new WebViewClient(){
                @Override
                public void onLoadResource(WebView view, String url) {
                    Logger.i("onLoadResource "+url);
                    super.onLoadResource(view, url);
                }

                @Override
                public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
                    Logger.i("onTooManyRedirects ");
                    super.onTooManyRedirects(view, cancelMsg, continueMsg);
                }

                @Override
                public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                    Logger.i("doUpdateVisitedHistory "+url);
                    super.doUpdateVisitedHistory(view, url, isReload);
                }

                @Override
                public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                    Logger.i("onReceivedHttpAuthRequest "+realm);
                    super.onReceivedHttpAuthRequest(view, handler, host, realm);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    Logger.i("shouldInterceptRequest "+url);
                    return super.shouldInterceptRequest(view, url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    Logger.i("onReceivedError url:" + failingUrl);
                }
                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    Logger.i("onReceivedSslError "+error.getUrl());
                    handler.proceed();
                }
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    Logger.i("onPageStarted url:"+url);
                }
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Logger.i("shouldOverrideUrlLoading url:"+url);
//                    WebView.HitTestResult hit = mWebView.getHitTestResult();
//                    int hitType = hit.getType();
//                    if (hitType == WebView.HitTestResult.SRC_ANCHOR_TYPE) {//点击超链接
//                        //这里执行自定义的操作
//                        return true;//返回true浏览器不再执行默认的操作
//                    }else if(hitType == 0){//重定向时hitType为0
//                        return false;//不捕获302重定向
//                    }else{
//                        return false;
//                    }
                    if(url.startsWith(ApiConfig.iermu_CODE_REDIRECT)) {
                        return true;
                    } else if(url.startsWith(ApiConfig.iermu_DEVTOKEN_REDIRECT)) {
                        return true;
                    } else if(url.startsWith(ApiConfig.pcs_REDIRECT)) {
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    Logger.i("onPageFinished url:"+url);
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }

        //mWebView.loadUrl("http://www.baidu.com");
        //mWebView.loadUrl(ApiConfig.getBaiduAuthorizeUrl());
        mWebView.loadUrl("http://123.57.4.235:8081/oauth/2.0/authorize?response_type=code&client_id=2222222222&redirect_uri=iermuconnect://code/success&scope=netdisk&display=mobile");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearFormData();
            mWebView.clearHistory();
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
            CookieSyncManager.createInstance(this);
            CookieManager.getInstance().removeAllCookie();
            CookieSyncManager.getInstance().sync();
        }
    }
}
