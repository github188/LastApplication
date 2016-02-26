package com.iermu.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.config.ApiConfig;
import com.iermu.ui.util.Util;

//
//public class WebActivity extends BaseActivity implements View.OnClickListener {
//import org.apache.http.cookie.Cookie;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.conn.DefaultClientConnection;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpParams;
//
//import java.net.SocketTimeoutException;
//import java.util.Date;
//import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends Activity implements View.OnClickListener {


    private static final String INTENT_PAGETYPE = "pageType";
    public static final String PAGE_BUY = "buy";
    public static final String PAGE_CVRBUY = "cvrbuy";
    public static final String PAGE_QUESTION = "question";
    public static final String PAGE_IERMU = "iermu";
    public static final String PAFE_REPORT = "report";
    public static final String PAGE_AGREEMENT = "agereement";
    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_UK = "uk";
    public static final String PAGE_SOLVE = "solve";
    public static final String PAGE_POSTER = "poster";
    public static final String BUG_CAM = "buycam";
    public static final String BAI_DU_URL = "baidu";
    public static final String FIND_PASSWORD = "password";
    public static final String USER_AGREEMENT = "agreeement";
    public static final String BIND_BAI_DU_URL = "bindbaidu";


    private WebView mWebView;
    TextView textView;
    ImageView setBack;
    RelativeLayout netError;
    TextView errorText;
    private String type;
    private ProgressBar progressBar;
    private String deviceId;
    private String uk;
    private String url;

    /**
     * 启动手机Web页面
     *
     * @param context
     * @param pageType
     */
    public static void actionStartWeb(Context context, String pageType) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_PAGETYPE, pageType);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void actionStartWeb(Context context, String pageType, String deviceId, String uk) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_PAGETYPE, pageType);
        intent.putExtra(KEY_DEVICE_ID, deviceId);
        intent.putExtra(KEY_UK, uk);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mWebView = (WebView) findViewById(R.id.web_view);
        setBack = (ImageView) findViewById(R.id.set_back);
        textView = (TextView) findViewById(R.id.tvTitle);
        netError = (RelativeLayout) findViewById(R.id.net_error);
        errorText = (TextView) findViewById(R.id.error_btn);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        errorText.setOnClickListener(this);
        setBack.setOnClickListener(this);
        url = "http://www.iermu.com/question4.php";
        initWebView(mWebView);
        Intent intent = getIntent();
        type = intent.getStringExtra(INTENT_PAGETYPE);
        deviceId = intent.getStringExtra(KEY_DEVICE_ID);
        uk = intent.getStringExtra(KEY_UK);
        showWeb(type);
        StatService.bindJSInterface(this, mWebView);
    }


    private void showWeb(String type) {
        if (type.equals(PAGE_BUY)) {
            if (Util.isNetworkConn(this)) {
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                textView.setText(R.string.page_bug);
                mWebView.loadUrl(ApiConfig.getCvrDev());
            } else {
                mWebView.setVisibility(View.GONE);
                netError.setVisibility(View.VISIBLE);
                textView.setText(R.string.page_bug);
            }
        } else if (type.equals(PAGE_CVRBUY)) {
            if (Util.isNetworkConn(this)) {
                textView.setText(R.string.page_cloud);
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                mWebView.loadUrl(ApiConfig.getCvrBug());
            } else {
                mWebView.setVisibility(View.GONE);
                netError.setVisibility(View.VISIBLE);
                textView.setText(R.string.page_cloud);
            }

        } else if (type.equals(PAGE_QUESTION)) {
            if (Util.isNetworkConn(this)) {
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                textView.setText(R.string.page_question);
                mWebView.loadUrl(ApiConfig.getQuestionNoemal());
            } else {
                mWebView.setVisibility(View.GONE);
                netError.setVisibility(View.VISIBLE);
                textView.setText(R.string.page_question);
            }
        } else if (type.equals(PAGE_IERMU)) {
            if (Util.isNetworkConn(this)) {
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                textView.setText(R.string.page_iermu);
                mWebView.loadUrl(ApiConfig.getOffical());
            } else {
                mWebView.setVisibility(View.GONE);
                netError.setVisibility(View.VISIBLE);
                textView.setText(R.string.page_iermu);
            }
        } else if (type.equals(PAFE_REPORT)) {
            if (Util.isNetworkConn(this)) {
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                textView.setText(R.string.page_report);
                String uid = ErmuBusiness.getAccountAuthBusiness().getUid();
                String accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
                String url = ApiConfig.getReportUrl(uid, accessToken, deviceId);
                mWebView.loadUrl(url);
            } else {
                mWebView.setVisibility(View.GONE);
                netError.setVisibility(View.VISIBLE);
                textView.setText(R.string.page_report);
            }
        } else if (type.equals(PAGE_AGREEMENT)) {
            if (Util.isNetworkConn(this)) {
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                textView.setText(R.string.page_agreement);
                mWebView.loadUrl(ApiConfig.getPublicLive());
            }
        } else if (type.equals(PAGE_SOLVE)) {
            if (Util.isNetworkConn(this)) {
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                textView.setText(R.string.page_solve);
                mWebView.loadUrl(ApiConfig.getSolve());
            }
        } else if (type.equals(PAGE_POSTER)) {
            if (Util.isNetworkConn(this)) {
                String webUrl = ErmuBusiness.getPreferenceBusiness().getPosterWebUrl();
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                textView.setText(R.string.page_poster);
                mWebView.loadUrl(webUrl);
            } else {
                mWebView.setVisibility(View.GONE);
                netError.setVisibility(View.VISIBLE);
                textView.setText(R.string.page_poster);
            }
        } else if (type.equals(BUG_CAM)) {
            if (Util.isNetworkConn(this)) {
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                textView.setText(R.string.page_bug);
                mWebView.loadUrl(ApiConfig.getBuyCam());
            } else {
                mWebView.setVisibility(View.GONE);
                netError.setVisibility(View.VISIBLE);
                textView.setText(R.string.page_bug);
            }
        } else if (type.equals(FIND_PASSWORD)){
            if (Util.isNetworkConn(this)){
                mWebView.setVisibility(View.VISIBLE);
                netError.setVisibility(View.GONE);
                textView.setText(R.string.find_password);
                mWebView.loadUrl(ApiConfig.getUpdatePassword());
            }
        }else if (type.equals(USER_AGREEMENT)){
            mWebView.setVisibility(View.VISIBLE);
            netError.setVisibility(View.GONE);
            textView.setText(R.string.user_agree);
            mWebView.loadUrl(ApiConfig.getUserDeal());
        } else if (type.equals(BIND_BAI_DU_URL)) {
            mWebView.setVisibility(View.VISIBLE);
            netError.setVisibility(View.GONE);
            textView.setText(R.string.bind_baidu);
            String token = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
            mWebView.loadUrl(ApiConfig.getBindBaidu(token));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back:
                if (type.equals(PAGE_POSTER)) {
                    startNextActivity();
                } else {
                    finish();
                }
                break;
            case R.id.error_btn:
                showWeb(type);
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (goBack()) {
                return true;
            }
            if (type.equals(PAGE_POSTER)) {
                startNextActivity();
            } else {
                if (mWebView != null) {
                    mWebView.clearHistory();
                    mWebView = null;
                }
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null)
            mWebView.destroy();
    }

    private void startNextActivity() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
                if (business.isLogin()) {
                    MainActivity.actionStartMain(WebActivity.this);
                } else {
                    LoginActivity.actionStartLogin(WebActivity.this);
                }
                finish();
            }
        }, 0);
    }


    /**
     * 设置Webview的WebviewClient
     *
     * @param webview webview
     */
    private void initWebView(WebView webview) {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        //设置加载进来的页面自适应手机屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                long time = progressBar.getDrawingTime();
                if (newProgress >= 100) {
                    progressBar.setVisibility(View.GONE);
                }
                progressBar.setProgress(newProgress);
            }
        });

        try {
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                }

                //				@Override
//				public boolean shouldOverrideUrlLoading(WebView view, String url) {
//					Uri content_url = Uri.parse(url);
//					boolean bShareDev = content_url.getQueryParameter("shareid")!=null && content_url.getQueryParameter("uk")!=null;
//					if(bShareDev){
//						Intent intent = new Intent(view.getContext(), pubcamPlayerActivity.class);
//			            intent.setAction("android.intent.action.VIEW");
//			            intent.setData(content_url);
//			            view.getContext().startActivity(intent);
//						return true;
//					}
//					return super.shouldOverrideUrlLoading(view, url);
//				}
//
                @Override
                public void onPageFinished(WebView view, String url) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean goBack() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }


}
