package com.iermu.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.config.AppConfig;
import com.iermu.client.listener.OnClientListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.NewPoster;
import com.iermu.client.util.FileUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.util.IntentUtil;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;



/**
 * Created by zhoushaopei on 15/6/15.
 */
public class SplashActivity extends BaseActivity implements OnClientListener {

    @ViewInject(R.id.new_poster)    ImageView mNewPoster;
    private boolean isNewPoster = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!this.isTaskRoot() && getIntent()!=null) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            //如果你就放在launcher Activity中话，这里可以直接return了
            Intent intent = getIntent();
            String action = intent.getAction();
            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }
        setContentView(R.layout.activity_splash);
        ViewHelper.inject(this);

        Intent intent = getIntent();
        if(IntentUtil.isIntentFromQDLT(intent)) {
            byte[] xxParam = intent.getByteArrayExtra("xxParam");
            com.unicom.oa.MD5 md5 = new com.unicom.oa.MD5();
            String decry    = md5.putMd5para(xxParam, AppConfig.QDLT_TOKEN);
            String result   = decry.substring(0, decry.indexOf("&"));
            IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
            if(!business.isQDLTLogin(result)) {
                BaiDuLoginActivity.actionQDLTAuth(this, result);
                finish();
                return;
            }
        }
        showNewPoster();
    }

    private void showNewPoster() {
        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.registerListener(OnClientListener.class, this);

        IPreferenceBusiness pre = ErmuBusiness.getPreferenceBusiness();
        String imgUrl = pre.getPosterImgUrl();
        String webUrl = pre.getPosterWebUrl();
        long startTime = pre.getPosterStartTime();
        long endTime = pre.getPosterEndTime();
        long time = System.currentTimeMillis() / 1000;
        boolean isShow = time > startTime && time < endTime;
        Bitmap bitmap = null;
        if (isShow) {
            bitmap = FileUtil.getDownFile(this, imgUrl.substring(imgUrl.lastIndexOf("/") + 1));
            if (bitmap != null) {
                mNewPoster.setVisibility(View.VISIBLE);
                mNewPoster.setImageBitmap(bitmap);
            } else {
                mNewPoster.setVisibility(View.GONE);
                business.getNewPoster(this);
            }
        } else {
            if (!TextUtils.isEmpty(imgUrl)) deleteFile(imgUrl.substring(imgUrl.lastIndexOf("/") + 1));
            business.getNewPoster(this);
        }
        startNextActivity(bitmap);
    }

    @OnClick(value = {R.id.new_poster})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_poster:
                isNewPoster = true;
                StatService.onEvent(this, "new_poster", "pass", 1);
                WebActivity.actionStartWeb(this, WebActivity.PAGE_POSTER);
                finish();
                break;
        }
    }

    private void startNextActivity(Bitmap bitmap) {
        if (bitmap != null) {
            ErmuApplication.execBackground(new Runnable() {
                @Override
                public void run() {
                    delay();
                }
            }, 3000);
        } else {
            ErmuApplication.execBackground(new Runnable() {
                @Override
                public void run() {
                    delay();
                }
            }, 500);
        }

    }

    private void delay() {
        if (!isNewPoster) {
            IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
            if (business.isLogin()) {
                MainActivity.actionStartMain(SplashActivity.this);
            } else {
                LoginActivity.actionStartLogin(SplashActivity.this);
            }
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.unRegisterListener(OnClientListener.class, this);
    }

    @Override
    public void onClient(Business bus, NewPoster client) {
        if (client != null) Logger.i("imgUrl:"+client.getImgUrl());
    }
}
