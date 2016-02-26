package com.iermu.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mobstat.StatService;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.config.AppConfig;
import com.iermu.client.listener.OnAbortAccountListener;
import com.iermu.client.listener.OnBaiduTokenInvalidListener;
import com.iermu.client.listener.onGrantShareListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.Logger;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.MainIermuFragment;
import com.iermu.ui.fragment.MainMessageFragment;
import com.iermu.ui.fragment.MainPersonalFragment;
import com.iermu.ui.fragment.MainPublicFragment;
import com.iermu.ui.fragment.publicchannel.PublicLiveFragment;
import com.iermu.ui.util.BaiduStatUtil;
import com.iermu.ui.util.CheckVersionUtils;
import com.iermu.ui.util.CmsTalk;
import com.iermu.ui.util.IntentUtil;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.view.AcceptAuthDialog;
import com.iermu.ui.view.CommonDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnCheckedChange;

import java.util.HashMap;
import java.util.Map;

/**
 * 主页面
 * 1.四个TabFragment切换
 * 2.直播页面
 *
 * @author wcy
 */
public class MainActivity extends BaseActionBarActivity implements onGrantShareListener, OnAbortAccountListener,OnBaiduTokenInvalidListener {

    private static final String INTENT_SHAREAUTH_URL = "grant";
    public static final String INTENT_NEW_MESSAGE = "newMessage";
    private Class currentTag = MainIermuFragment.class;
    private static boolean isExit = false;
    @SuppressLint("UseSparseArrays")
    private Map<Class, Fragment> mFragments = new HashMap<Class, Fragment>();

    @ViewInject(R.id.maintab_iermu)
    RadioButton mTabIermu;
    @ViewInject(R.id.maintab_message)
    RadioButton mTabMessage;
    @ViewInject(R.id.maintab_public)
    RadioButton mTabPublic;

    static {
        try {
            //羚羊云SDK
//            System.loadLibrary("CloudPlatformAPI");
//            System.loadLibrary("CloudService");
//            System.loadLibrary("crypto");
//            System.loadLibrary("ffmpeg");
//            System.loadLibrary("jplayer");
//            System.loadLibrary("ssl");
        } catch (UnsatisfiedLinkError e) {
            Logger.e("load library failed", e);
        }
    }

    /**
     * 启动主页面
     *
     * @param ctx
     */
    public static void actionStartMain(Activity ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
        ctx.startActivity(intent);
        ActivityStackHelper.clearActivityStack();
        //if (ctx != null) ctx.finish();
    }

    /**
     * 接受授权邀请、启动主页面
     *
     * @param ctx
     * @param extInfo
     */
    public static void actionShareAuth(Activity ctx, String extInfo) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.putExtra(INTENT_SHAREAUTH_URL, extInfo);
        //ctx.overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
        ctx.startActivity(intent);
        ActivityStackHelper.clearActivityStack();
        //if (ctx != null) ctx.finish();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(!this.isTaskRoot() && getIntent()!=null) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
//            //如果你就放在launcher Activity中话，这里可以直接return了
//            Intent intent = getIntent();
//            String action = intent.getAction();
//            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
//                finish();
//                return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
//            }
//        }
        setContentView(R.layout.activity_main);
        ViewHelper.inject(this);
        if (savedInstanceState == null) {
            initMainTabFragment();
            mTabIermu.performClick();
        } else {
            initMainTabFragment();
            mTabIermu.performClick();
        }

        CheckVersionUtils.checkAlertUpdateVersion(this);
        ErmuBusiness.getAccountAuthBusiness().registerListener(OnAbortAccountListener.class, this);
        registerReceiver(mNewAlarmReceiver, new IntentFilter(ErmuApplication.INTENT_NEW_ALARM));
        ErmuBusiness.getAccountAuthBusiness().registerListener(OnBaiduTokenInvalidListener.class, this);
        ErmuBusiness.getShareBusiness().registerListener(onGrantShareListener.class, this);
        // 分享初始化
        ShareUtil.initShareSdk(this);
        BaiduStatUtil.initBaiduStat(this);
        ErmuApplication.startPushWork();

        new CmsTalk();
        handleIntent(getIntent());
//        getShareCode(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
//        getShareCode(intent);
    }

//    private void getShareCode(Intent intent) {
//        if (intent.hasExtra(INTENT_SHAREAUTH_URL)) {
//            String extra = intent.getStringExtra(INTENT_SHAREAUTH_URL);
//            Uri uri = Uri.parse(extra); //String extra = "http://www.iermu.com/grant?code=e38754bf0f67d36f270ed3fafcb3b53a&desc=小杜胡哈哈&uname=小度";
//            String code = uri.getQueryParameter("code");
//            String desc = uri.getQueryParameter("desc");
//            String uname = uri.getQueryParameter("uname");
//            IAccountAuthBusiness authBusiness = ErmuBusiness.getAccountAuthBusiness();
//            if (authBusiness.isLogin()) {//判断登陆状态
//                //ErmuApplication.toast("接受邀请 uname:"+uname);
//                ICamShareBusiness business = ErmuBusiness.getShareBusiness();
//                business.registerListener(onGrantShareListener.class, this);
//                showAcceptAuth(uname, desc, code);
//            } else {
//                finish();
//                LoginActivity.actionShareAuth(this, extra); //启动登录页面
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        playShareDev();
        StatService.onResume(this);

        if (getIntent().getStringExtra(INTENT_NEW_MESSAGE) != null) {
            switchMessageFragment();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNewAlarmReceiver);
        ErmuBusiness.getShareBusiness().unRegisterListener(onGrantShareListener.class, this);
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnAbortAccountListener.class, this);
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnBaiduTokenInvalidListener.class, this);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            ErmuApplication.toast(getResources().getString(R.string.exit_app));
            handler.sendEmptyMessageDelayed(0, 3000);
        } else {
            finish();
            System.exit(0);
        }
    }

    @OnCheckedChange(R.id.maintab_radiogoup)
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.maintab_iermu:
                switchFragment(R.id.maintab_fragment, MainIermuFragment.class);
                break;
            case R.id.maintab_message:
                // 判断，如果切换到消息页面，去掉小红点
                //清除小红点
                //清除通知栏
                mTabMessage.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.iermu_message_selector), null, null);
                Intent intent = new Intent();
                intent.setAction(ErmuApplication.INTENT_NOTICATION_PUSH_CLEAR);
                sendBroadcast(intent);
                switchFragment(R.id.maintab_fragment, MainMessageFragment.class);
                break;
            case R.id.maintab_public:
                switchFragment(R.id.maintab_fragment, MainPublicFragment.class);
                break;
            case R.id.maintab_personal:
                switchFragment(R.id.maintab_fragment, MainPersonalFragment.class);
                break;
        }
    }

    /**
     * 切换到公共频道
     */
    public void switchPubFragment() {
        if (mTabPublic != null) mTabPublic.performClick();
    }

    /**
     * 切换到消息频道
     */
    public void switchMessageFragment() {
        if (mTabMessage != null) mTabMessage.performClick();
    }

    public void switchMainFragment() {
        if (mTabIermu != null) mTabIermu.performClick();
    }

    @Override
    public void onBackPressed() {
        exit();
        return;
    }

    /**
     * 初始化底部导航Fragment
     */
    private void initMainTabFragment() {
        Fragment iermu = MainIermuFragment.actionInstance(this);
        Fragment message = MainMessageFragment.actionInstance(this);
        Fragment pub = MainPublicFragment.actionInstance(this);
        Fragment personal = MainPersonalFragment.actionInstance(this);

        mFragments.put(MainIermuFragment.class, iermu);
        mFragments.put(MainMessageFragment.class, message);
        mFragments.put(MainPublicFragment.class, pub);
        mFragments.put(MainPersonalFragment.class, personal);
    }

    private void switchFragment(int containerId, Class fragmentTag) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment target = getFragment(fragmentTag);
        Fragment source = getFragment(currentTag);
        boolean same = (fragmentTag == currentTag);
        if (!same && source != null && !source.isDetached() && source.isAdded()) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.hide(source);
            transaction.commitAllowingStateLoss();
        }
        if (target != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            if (target.isDetached()) {
                transaction.attach(target);
            } else if (target.isHidden()) {
                transaction.show(target);
            } else if (!target.isAdded()) {
                transaction.add(containerId, target, String.valueOf(fragmentTag));
            }
            transaction.commitAllowingStateLoss();
        }
        currentTag = fragmentTag;
    }

    private Fragment getFragment(Class fragmentId) {
        String fragmentTag = String.valueOf(fragmentId);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            fragment = mFragments.get(fragmentId);
        }
        return fragment;
    }

    private AcceptAuthDialog authDialog;
    private void showAcceptAuth(String uuName, String deviceName, final String code) {
        if (authDialog != null && authDialog.isShowing()) {
            authDialog.dismiss();
        }
        authDialog = new AcceptAuthDialog(this, uuName, deviceName);
        authDialog.setClicklistener(new AcceptAuthDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                authDialog.dismiss();
                ErmuBusiness.getShareBusiness().grantShare(code);
            }

            @Override
            public void doCancel() {
                authDialog.dismiss();
            }
        });
        authDialog.show();
    }

    // 第三方分享播放
    private void playShareDev() {
        Uri uriPath = getIntent().getData();
        if (uriPath == null) return;

        String scheme = uriPath.getScheme();
        if (scheme == null) return;
        // 分享打开，获取播放地址，直接打开播放画面
        String shareId = uriPath.getQueryParameter("shareid");
        String uk = uriPath.getQueryParameter("uk");
        Fragment fragment = PublicLiveFragment.actionInstance(null, shareId, uk, true);
        BaseFragment.addToBackStack(this, fragment, true);
    }

    @Override
    public void onGrantShare(Business bus, String uid, String userName) {
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                ErmuApplication.toast(getResources().getString(R.string.AUTH_SUCCESS));
                break;
            case BusinessCode.GRANT_INVALID:
                ErmuApplication.toast(getResources().getString(R.string.AUTH_GRANT_INVALID));
                break;
            case BusinessCode.GRANT_USED:
                ErmuApplication.toast(getResources().getString(R.string.AUTH_GRANT_USED));
                break;
            case BusinessCode.NOT_GRANT_SELF:
                ErmuApplication.toast(getResources().getString(R.string.AUTH_NOT_GRANT_SELF));
                break;
            case BusinessCode.DEVICE_GRANT_FAILED:
                ErmuApplication.toast(getResources().getString(R.string.DEVICE_GRANT_FAILED));
                break;
            default:
                ErmuApplication.toast(getResources().getString(R.string.DEVICE_GRANT_FAILED));
                break;
        }
    }

    @Override
    public void onAbortAccount(Business bus) {
        String content = getString(R.string.abort_account_apicosed);
        int code = bus.getCode();
        if (code == BusinessCode.ACCESS_TOKEN_INVALID) {
            content = getString(R.string.abort_account_token_);
        }
        final CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog
                .setTitle(getString(R.string.abort_account_title))
                .setContent(content)
                .setOkText(getResources().getString(R.string.dialog_confirm))
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonDialog.dismiss();
                        // 清除WebView页面缓存Cookie
                        CookieSyncManager.createInstance(MainActivity.this);
                        CookieManager.getInstance().removeAllCookie();
                        CookieSyncManager.getInstance().sync();
                        LoginActivity.actionStartLogin(MainActivity.this);
                        finish();
                    }
                }).show();
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra(INTENT_SHAREAUTH_URL)) {                     //分享授权码(接收邀请)
            //String extra = "http://www.iermu.com/grant?code=e38754bf0f67d36f270ed3fafcb3b53a&desc=小杜胡哈哈&uname=小度";
            String extra    = intent.getStringExtra(INTENT_SHAREAUTH_URL);
            Uri uri         = Uri.parse(extra);
            String code     = uri.getQueryParameter("code");
            String desc     = uri.getQueryParameter("desc");
            String uname    = uri.getQueryParameter("uname");
            IAccountAuthBusiness authBusiness = ErmuBusiness.getAccountAuthBusiness();
            if (authBusiness.isLogin()) {//判断登陆状态
                Logger.i("接受邀请 uname:"+uname);
                showAcceptAuth(uname, desc, code);
            } else {
                finish();
                LoginActivity.actionShareAuth(this, extra); //启动登录页面
            }
        } else if(IntentUtil.isIntentFromQDLT(intent)) {
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
    }

    private BroadcastReceiver mNewAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //null || 不是推送小红点广播
            if(intent == null || !ErmuApplication.INTENT_NEW_ALARM.equals(intent.getAction())) return;

            if(currentTag != MainMessageFragment.class && mTabMessage != null) {
                mTabMessage.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.iermu_message_reddot), null, null);
            } else if(MainActivity.this !=null ){
                String deviceId = intent.getStringExtra("deviceId");
                MainMessageFragment fragment = (MainMessageFragment) getFragment(MainMessageFragment.class);
                if(fragment!=null) fragment.updateData(deviceId);
            }
        }
    };

    @Override
    public void onBaiduTokenInvalid(Business business) {
        if(business.getCode() == BusinessCode.ACCESS_TOKEN_INVALID && business.getConnectType() == ConnectType.BAIDU) {
            BaiDuLoginActivity.actionStartBaiduLogin(this, true);
            finish();
        }
    }
}
