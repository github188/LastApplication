package com.iermu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.ui.util.BaiduStatUtil;
import com.iermu.ui.util.CheckVersionUtils;
import com.iermu.ui.util.Util;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

/**
 * 登录页面(体验、登录)
 *
 * Created by zhoushaopei on 15/6/15.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String INTENT_SHAREAUTH_URL = "grant";
    @ViewInject(R.id.login_btn)     Button mLoginBtn;
    @ViewInject(R.id.guester_btn)   Button mGuesterBtn;

    public static void actionStartLogin(Context ctx) {
        Intent intent = new Intent(ctx, LoginActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * 接受邀请, 需要先登录账号
     * @param ctx
     * @param shareAuthUrl
     */
    public static void actionShareAuth(Context ctx, String shareAuthUrl) {
        Intent intent = new Intent(ctx, LoginActivity.class);
        intent.putExtra(INTENT_SHAREAUTH_URL, shareAuthUrl);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_login);
        ViewHelper.inject(this);
        mLoginBtn.setOnClickListener(this);
        mGuesterBtn.setOnClickListener(this);
        BaiduStatUtil.initBaiduStat(this);
        CheckVersionUtils.checkAlertUpdateVersion(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.login_btn:
            if(Util.isNetworkConn(this)){
                Intent intent = getIntent();
                if(intent.hasExtra(INTENT_SHAREAUTH_URL)) {
                    String extra = intent.getStringExtra(INTENT_SHAREAUTH_URL);
                    BaiDuLoginActivity.actionShareAuth(this, extra);
                } else {
                    BaiDuLoginActivity.actionStartBaiduLogin(this, false);
                }
            } else {
                ErmuApplication.toast(getResources().getString(R.string.check_network));
            }
            break;
        case R.id.guester_btn:
            PubCamListActivity.actionPubCamList(this);
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
