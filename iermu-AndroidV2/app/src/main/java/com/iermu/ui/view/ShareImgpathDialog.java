package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.util.Logger;
import com.iermu.ui.util.Util;
import com.mob.tools.utils.UIHandler;

import java.lang.reflect.Method;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


/**
 * Created by xjy on 15/8/4.
 */
public class ShareImgpathDialog extends Dialog implements View.OnClickListener, Handler.Callback, PlatformActionListener {

    private static final int MSG_TOAST = 1;
    private static final int MSG_ACTION_CCALLBACK = 2;
    private static final int MSG_CANCEL_NOTIFY = 3;

    private Context context;
    private String imagePath;   //本地路径
    private String imageUrl;    //网络路径
    private String title;
    private String url;
    private String content;
    private int shareType;
    private Button cancel;
    private Button shareMoments;
    private Button shareWx;
    private RelativeLayout share;


    protected ShareImgpathDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public ShareImgpathDialog(Context ctx, int theme, String imgpath) {
        super(ctx, R.style.load_dialog);
        this.context = ctx;
        this.imagePath = imgpath;
        this.shareType = Platform.SHARE_IMAGE;
    }

    public ShareImgpathDialog(Context ctx, int theme, String title, String url, String imageUrl, String content) {
        super(ctx, R.style.load_dialog);
        this.context = ctx;
        this.imageUrl = imageUrl;
        this.title = title;
        this.url = url;
        this.content = content;
        this.shareType = Platform.SHARE_WEBPAGE;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();

    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View view = inflater.inflate(R.layout.share_fragment, null);
        setContentView(view, params);
        RelativeLayout shareLay = (RelativeLayout) view.findViewById(R.id.share_layout);
        share = (RelativeLayout) view.findViewById(R.id.share_type);
        shareWx = (Button) view.findViewById(R.id.share_wechat);
        shareMoments = (Button) view.findViewById(R.id.share_moments);
        cancel = (Button) view.findViewById(R.id.share_cancel);
        shareWx.setOnClickListener(this);
        shareMoments.setOnClickListener(this);
        cancel.setOnClickListener(this);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getContext().getResources().getDisplayMetrics();
        lp.width = d.widthPixels;
        lp.height = d.heightPixels;
        dialogWindow.setAttributes(lp);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int y = (int) event.getY();
                int top = share.getTop();
                if (y < top) dismiss();
                return false;
            }
        });
        boolean hasNavigationBar = deviceHasNavigationBar(context);
        int navigationBarHeight = getNavigationBarHeight(context);

        if (hasNavigationBar) {
            Logger.i("navigation:hava"+navigationBarHeight);
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params1.setMargins(0,0,0,144);
            shareLay.setLayoutParams(params1);
        } else {
            Logger.i("navigation:no");
        }

    }

    public boolean checkDeviceHasNavigationBar(Context activity) {
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            return true;
        }
        return false;
    }

    private static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && deviceHasNavigationBar(context)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    private static boolean deviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            Logger.i(e.toString());
        }

        return hasNavigationBar;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_wechat:
                shareWx.setClickable(false);
                Platform.ShareParams wechat = shareParams();
                Platform weixin = ShareSDK.getPlatform(context, Wechat.NAME);
                weixin.setPlatformActionListener(this);
                weixin.share(wechat);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareWx.setClickable(true);
                    }
                }, 2000);
                break;
            case R.id.share_moments:
                shareMoments.setClickable(false);
                Platform.ShareParams wechatMoments = shareParams();
                Platform wXP = ShareSDK.getPlatform(context, WechatMoments.NAME);
                wXP.setPlatformActionListener(this);
                wXP.share(wechatMoments);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareMoments.setClickable(true);
                    }
                }, 200);
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    private Platform.ShareParams shareParams() {
        Platform.ShareParams wechat = new Platform.ShareParams();
        if(shareType == Platform.SHARE_WEBPAGE) {
            wechat.setTitle(title);
            wechat.setText(content);
            wechat.setUrl(url);
            wechat.setImageUrl(imageUrl);
            wechat.setShareType(Platform.SHARE_WEBPAGE);
        } else if(shareType == Platform.SHARE_IMAGE){
            wechat.setImagePath(imagePath);
            wechat.setShareType(Platform.SHARE_IMAGE);
        } else {
            //TODO
        }
        return wechat;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.arg1) {
            case 1: { // 成功
//                ErmuApplication.toast(context.getString(R.string.share_success));
            }
            break;
            case 2: { // 失败
//                ErmuApplication.toast(context.getString(R.string.share_fail));
            }
            break;
            case 3: { // 取消
//                ErmuApplication.toast(context.getString(R.string.cancel_share));
            }
            break;
        }
        return false;
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> stringObjectHashMap) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onError(Platform platform, int action, Throwable t) {
        t.printStackTrace();
        //错误监听,handle the error msg
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = t;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onCancel(Platform platform, int action) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }
}
