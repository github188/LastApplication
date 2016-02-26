package com.iermu.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.config.AppConfig;
import com.iermu.client.listener.OnAccountAuthListener;
import com.iermu.client.model.Business;
import com.iermu.client.util.Logger;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.CommonDialog;


/**
 * 百度Web登录页面
 * 1. 跳转到百度登录页面
 * 2. 登录成功，得到授权码
 * 3. 获取百度AccessToken
 * 4. 成功 登录首页
 * 5. 失败 提示需要重新登录
 * <p>
 * Created by zhoushaopei on 15/6/15.
 */
public class BaiDuLoginActivity extends BaseActivity implements OnAccountAuthListener, View.OnClickListener {

    private static final String INTENT_SHAREAUTH_URL = "grant";
    private static final String INTENT_QDLT_AUTH_PARAM = "QDLT_AUTH_PARAM";
    private static final String IS_LOGIN_AGAIN = "login";
    private WebView mWebView;
    private boolean isGetToken = false; //正在获取Token
    private boolean isDevToken = false; //正在获取设备Token
    CommonCommitDialog webLoad;
    CommonDialog commonDialog;
    ImageView mSetBack;
    private ProgressBar progressBar;
    RelativeLayout netError;
    TextView netErrorBtn;

    /**
     * 启动百度Web登录页面
     *
     * @param ctx
     */
    public static void actionStartBaiduLogin(Context ctx, boolean isLoginAgain) {
        Intent intent = new Intent(ctx, BaiDuLoginActivity.class);
        intent.putExtra(IS_LOGIN_AGAIN, isLoginAgain);
        ctx.startActivity(intent);
    }

    /**
     * 接受邀请, 需要先登录账号
     *
     * @param ctx
     * @param shareAuthUrl
     */
    public static void actionShareAuth(Context ctx, String shareAuthUrl) {
        Intent intent = new Intent(ctx, BaiDuLoginActivity.class);
        intent.putExtra(INTENT_SHAREAUTH_URL, shareAuthUrl);
        ctx.startActivity(intent);
    }

    /**
     * 青岛联通绑定百度账号
     * @param ctx
     * @param param 手机号
     */
    public static void actionQDLTAuth(Context ctx, String param) {
        Intent intent = new Intent(ctx, BaiDuLoginActivity.class);
        intent.putExtra(INTENT_QDLT_AUTH_PARAM, param);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
        Intent intent = getIntent();
        boolean isAgainLogin = intent.getBooleanExtra(IS_LOGIN_AGAIN, false);
        if(isAgainLogin) showLoginAgainDialog();

        if(intent.hasExtra(INTENT_QDLT_AUTH_PARAM)) {
            String param    = intent.getStringExtra(INTENT_QDLT_AUTH_PARAM);
            long signExpire = AppConfig.getQDLTSignExpire();
            String authSign = AppConfig.getQDLTAuthSign(signExpire, param);
            mWebView.loadUrl(ApiConfig.getQDLTAuthorizeUrl(param, signExpire, authSign));
        } else {
            mWebView.loadUrl(ApiConfig.getIermuAuthorizeUrl());
        }
    }

    private void showLoginAgainDialog() {
        commonDialog = new CommonDialog(this);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog
            .setTitle("")
            .setContent("您的百度账号授权已失效，请重新登录并授权")
            .setOkText(getResources().getString(R.string.dialog_confirm))
            .setOkListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commonDialog.dismiss();
                }
            }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ErmuBusiness.getAccountAuthBusiness().registerListener(OnAccountAuthListener.class, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnAccountAuthListener.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //clearWebView();
        if(mWebView != null) {
            mWebView.destroy();
        }
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnAccountAuthListener.class, this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (goBack()) {
                return true;
            } else {
                finish();
                clearWebView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.set_back:
            finish();
            clearWebView();
            break;
        case R.id.error_btn:
            progressBar.setVisibility(View.VISIBLE);
            netError.setVisibility(View.GONE);
            clearWebView();
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(ApiConfig.getIermuAuthorizeUrl());
            break;
        }
    }



    @Override
    public void onLoginSuccess(Business bus) {
        isGetToken = false;
        isDevToken = false;
        if (!bus.isSuccess()) {
            clearWebViewCache();
            ErmuApplication.toast(getResources().getString(R.string.abort_account_fail) + "("+bus.getErrorCode()+")");
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra(INTENT_SHAREAUTH_URL)) {
                String extra = intent.getStringExtra(INTENT_SHAREAUTH_URL);
                MainActivity.actionShareAuth(this, extra);
            } else {
                MainActivity.actionStartMain(this);
            }
            if (mWebView != null) {
                mWebView.clearCache(true);
                mWebView.clearFormData();
                mWebView.clearHistory();
                //mWebView.destroy();
                //deleteDatabase("webview.db");
                //deleteDatabase("webviewCache.db");
                //CookieSyncManager.createInstance(this);
                //CookieManager.getInstance().removeAllCookie();
                //CookieSyncManager.getInstance().sync();
            }
        }
        if (webLoad != null) webLoad.dismiss();
    }

    private void initView() {
        mSetBack    = (ImageView) findViewById(R.id.set_back);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView    = (WebView) findViewById(R.id.web_view);
        netError    = (RelativeLayout) findViewById(R.id.net_error);
        netErrorBtn = (TextView) findViewById(R.id.error_btn);

        webLoad = new CommonCommitDialog(BaiDuLoginActivity.this);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //设置加载进来的页面自适应手机屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        mSetBack.setOnClickListener(this);
        netErrorBtn.setOnClickListener(this);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Logger.i(newProgress + "newProgress");
                if (newProgress >= 100) {
                    progressBar.setVisibility(View.GONE);
                }
                progressBar.setProgress(newProgress);
            }
        });
        try {
            mWebView.setWebViewClient(mWebViewClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean goBack() {
        WebView webView = mWebView;
        String url = webView.getUrl();
        Logger.i("goBack " + url);
        if (webView != null
                && webView.canGoBack()
                && !ApiConfig.isIermuAuthorizeUrl(url)      //判断是否为百度登录页面
                && !ApiConfig.isIermuDeviceTokenUrl(url)
                && !ApiConfig.isBaiduAuthorizeUrl(url)) {

            if (mWebView != null) {
                mWebView.clearCache(true);
                mWebView.clearFormData();
                CookieSyncManager.createInstance(this);
                CookieManager.getInstance().removeAllCookie();
                CookieSyncManager.getInstance().sync();
            }

            webView.goBack();
            return true;
        }
        return false;
    }

    //不清除浏览器历史记录(网页打开记录)
    private void clearWebViewCache() {
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearFormData();
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
            CookieSyncManager.createInstance(this);
            CookieManager.getInstance().removeAllCookie();
            CookieSyncManager.getInstance().sync();
        }
    }

    private void clearWebView() {
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearFormData();
            mWebView.clearHistory();
            //mWebView.destroy();
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
            CookieSyncManager.createInstance(this);
            CookieManager.getInstance().removeAllCookie();
            CookieSyncManager.getInstance().sync();
        }
    }

    //处理Webview 重定向请求
    private void handleWebviewInterceptRequest(String url) {
        Logger.i("handleWebviewInterceptRequest url:" + url+"  \r\n ------------->" +
                " isGetToken:"+isGetToken+" isDevToken"+isDevToken);
        if (!isGetToken && ApiConfig.isIermuCodeRedirectUrl(url)) {
            isGetToken = true;
            isDevToken = false;
            webLoad.show();
            webLoad.setOnCancelListener(new DialogInterface.OnCancelListener(){
                @Override
                public void onCancel(DialogInterface dialog) {
                    isGetToken = false;
                    isDevToken = false;
                }
            });
            webLoad.setStatusText(getResources().getString(R.string.dialog_commit_text));
            Intent intent = getIntent();
            if(intent.hasExtra(INTENT_QDLT_AUTH_PARAM)) {
                String param = intent.getStringExtra(INTENT_QDLT_AUTH_PARAM);
                ErmuBusiness.getAccountAuthBusiness().getAccessTokenAsQDLT(url, param);
            } else {
                ErmuBusiness.getAccountAuthBusiness().getAccessToken(url);
            }
        }
//        else if (!isGetToken && ApiConfig.isBaiduCodeRedirectUrl(url)) {
//            webLoad.show();
//            webLoad.setOnCancelListener(new DialogInterface.OnCancelListener(){
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    isGetToken = false;
//                    isDevToken = false;
//                }
//            });
//            webLoad.setStatusText(getResources().getString(R.string.dialog_commit_text));
//            ErmuBusiness.getBaiduAuthBusiness().getAccessToken(url);
//        }
    }

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Logger.i("onReceivedError url:" + failingUrl);  //网页加载失败, 会走这里的回调
            if(ApiConfig.isBaiduAuthorizeUrl(failingUrl)) { //服务器重定向到百度登录页面
                mWebView.loadUrl("");
                if(webLoad!=null) webLoad.dismiss();
                mWebView.setVisibility(View.INVISIBLE);
                netError.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //Logger.i("onPageStarted url:" + url);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Logger.i("shouldOverrideUrlLoading url:" + url);
            if (url.startsWith(ApiConfig.iermu_CODE_REDIRECT)) {
                handleWebviewInterceptRequest(url);
                return true;
            } else if (url.startsWith(ApiConfig.iermu_DEVTOKEN_REDIRECT)) {
                handleWebviewInterceptRequest(url);
                return true;
            } else if (url.startsWith(ApiConfig.pcs_REDIRECT)) {
                handleWebviewInterceptRequest(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.INVISIBLE);
            Logger.i("onPageFinished url:" + url);
            if (url.contains(ApiConfig.getIermuAuthorizeUrl())
                    || url.contains("http://123.57.4.235:8081/oauth/2.0/connect/success?code")  //异常 //http://123.57.4.235:8081/oauth/2.0/connect/success?code=023615f9cc8778fb0dd078db01a92848&state=eyJjbGllbnRfaWQiOiIyMjIyMjIyMjIyIiwicmVzcG9uc2VfdHlwZSI6ImNvZGUiLCJyZWRpcmVjdF91cmkiOiJpZXJtdWNvbm5lY3Q6XC9cL2NvZGVcL3N1Y2Nlc3MiLCJzdGF0ZSI6bnVsbCwic2NvcGUiOiJuZXRkaXNrIiwiZGlzcGxheSI6Im1vYmlsZSIsImZvcmNlX2xvZ2luIjoxLCJjb25maXJtX2xvZ2luIjpudWxsLCJsb2dpbl90eXBlIjpudWxsLCJjb25uZWN0X3R5cGUiOiIxIn0%3D#-3#1
                    || url.contains("https://openapi.baidu.com/oauth/2.0authorize?client_id")) {
                //ErmuApplication.toast("服务器回传地址: " + url);
                mWebView.loadUrl("");
                if(webLoad!=null) webLoad.dismiss();
                mWebView.setVisibility(View.INVISIBLE);
                netError.setVisibility(View.VISIBLE);
                return;
            }
            handleWebviewInterceptRequest(url);
        }
    };

}
