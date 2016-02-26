package com.iermu.ui.fragment.MineIermu;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.mobstat.StatService;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.util.PhoneDevUtil;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.CapsuleUtil;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.DimenUtils;
import com.iermu.ui.view.ShareImgpathDialog;
import com.mob.tools.utils.UIHandler;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import tv.cjump.jni.DeviceUtils;

/**
 * Created by zhoushaopei on 15/11/24.
 */
public class CapsuleFragment extends BaseFragment implements PlatformActionListener, Handler.Callback, View.OnTouchListener {

    private static String TEMPERATURE   = "tem";
    private static String HUMIDITY      = "hum";
    private static String TIME          = "time";
    private static String BITMAP        = "bitmap";
    private static final int MSG_ACTION_CCALLBACK = 2;

    @ViewInject(R.id.temperature)       TextView mTemperature;
    @ViewInject(R.id.humidity)          TextView mHumidity;
    @ViewInject(R.id.temperature_level) TextView mTemperatureLevel;
    @ViewInject(R.id.humidity_level)    TextView mHumidityLevel;
    @ViewInject(R.id.img_temperature)   ImageView mTemImg;
    @ViewInject(R.id.img_humidity)      ImageView mHumImg;
    @ViewInject(R.id.tips)              TextView mTips;
    @ViewInject(R.id.view_pic)          LinearLayout mViewPic;
    @ViewInject(R.id.share_capsule)     Button mShareCapsule;
    @ViewInject(R.id.cache_img)         ImageView mCacheImg;
    @ViewInject(R.id.share_trans)       View mShareTrans;
    @ViewInject(R.id.share_wechat)      Button mShareWx;
    @ViewInject(R.id.share_moments)     Button mShareMoments;
    @ViewInject(R.id.share_type)        RelativeLayout mShareType;

    private long currentTime;
    private Bitmap bitmap;
    private boolean isShareVisible = false;

    public static Fragment actionInstance(Context context, int temNum, int humNum, long time, Bitmap bitmap) {

        CapsuleFragment fragment = new CapsuleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TEMPERATURE, temNum);
        bundle.putInt(HUMIDITY, humNum);
        bundle.putLong(TIME, time);
        bundle.putParcelable(BITMAP, bitmap);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.capsule_title);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_capsule, null);
        ViewHelper.inject(this, view);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();

        return view;
    }

    private void initView () {
        mShareTrans.setOnTouchListener(this);
        Bundle bundle = getArguments();
        int tem = bundle.getInt(TEMPERATURE);
        int hum = bundle.getInt(HUMIDITY);
        long time = bundle.getLong(TIME);
        Bitmap bitmap = bundle.getParcelable(BITMAP);
        int temNum = CapsuleUtil.temNum(tem);
        int humNum = CapsuleUtil.humNum(hum);
        int screenWidth = PhoneDevUtil.getScreenWidth(getActivity()) - DimenUtils.dip2px(getActivity(), 16);
        int imgHeight = screenWidth * 9 /16;
        ViewGroup.LayoutParams params = mCacheImg.getLayoutParams();
        params.width = screenWidth;
        params.height = imgHeight;
        mCacheImg.setLayoutParams(params);
        mCacheImg.setImageBitmap(bitmap);
        mTemperature.setText(tem+"°C");
        mHumidity.setText(hum+"%");

        Resources resources = getActivity().getResources();
        String[] temArray = resources.getStringArray(R.array.temperature_level);
        String[] humArray = resources.getStringArray(R.array.humidity_level);
        String[] temArrayTxt = resources.getStringArray(R.array.temperature_text);
        String[] humArrayTxt = resources.getStringArray(R.array.humidity_text);
        mTemperatureLevel.setText(temArray[temNum-1]);
        mHumidityLevel.setText(humArray[humNum-1]);
        mTips.setText(temArrayTxt[temNum-1] + "\n" + humArrayTxt[(humNum-1)]);

        if (humNum == 3) {
            mHumImg.setBackgroundResource(R.drawable.humidity_green);
        } else if (humNum == 1 || humNum == 4) {
            mHumImg.setBackgroundResource(R.drawable.humidity_red);
        } else if (humNum == 2) {
            mHumImg.setBackgroundResource(R.drawable.humidity_yellow);
        }
        if (temNum == 5) {
            mTemImg.setBackgroundResource(R.drawable.temperature_green);
        } else if (temNum == 1 || temNum == 2 || temNum == 8 || temNum == 9 || temNum == 10)  {
            mTemImg.setBackgroundResource(R.drawable.temperature_red);
        } else if (temNum == 3 || temNum == 4 || temNum == 6 || temNum == 7) {
            mTemImg.setBackgroundResource(R.drawable.temperature_yellow);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = convertViewToBitmap(mViewPic);
                currentTime = new Date().getTime();
                ShareUtil.saveCapsuleBitmap(bitmap,currentTime);
            }
        }, 200);
    }

    @OnClick(value = {R.id.share_capsule, R.id.share_wechat, R.id.share_moments, R.id.share_cancel})
    private void onClick(View view) {
        switch (view.getId()) {
        case R.id.share_capsule:
            StatService.onEvent(getActivity(), "capsule_id", "pass", 1);
            mShareCapsule.setEnabled(false);
            if (bitmap != null) {
                String imagePath = ShareUtil.getCapsuleImgPath(currentTime);
//                ShareUtil.capsuleShare(getActivity(), imagePath);
//                animationIn(imagePath);
                aniIn();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mShareCapsule.setEnabled(true);
                }
            }, 200);
            break;
        case R.id.share_wechat:
            mShareWx.setClickable(false);
            Platform.ShareParams wechat = shareParams();
            Platform weixin = ShareSDK.getPlatform(getActivity(), Wechat.NAME);
            weixin.setPlatformActionListener(this);
            weixin.share(wechat);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mShareWx.setClickable(true);
                }
            }, 300);
            break;
        case R.id.share_moments:
            mShareMoments.setClickable(false);
            Platform.ShareParams wechatMoments = shareParams();
            Platform wXP = ShareSDK.getPlatform(getActivity(), WechatMoments.NAME);
            wXP.setPlatformActionListener(this);
            wXP.share(wechatMoments);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mShareMoments.setClickable(true);
                }
            }, 300);
            break;
        case R.id.share_cancel:
            animationOut();
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isShareVisible) {
                animationOut();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(bitmap!=null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public Bitmap convertViewToBitmap(View view){
        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void animationIn(String imagePath) {
        ShareImgpathDialog dialog = new ShareImgpathDialog(getActivity(), R.style.custom_dialog, imagePath);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.dialogAnim_style);  //添加动画
        window.setBackgroundDrawableResource(R.drawable.custom_bg);
        dialog.show();
    }

    private void aniIn() {
        mShareCapsule.setVisibility(View.GONE);
        isShareVisible = true;
        mShareType.setVisibility(View.VISIBLE);
        mShareTrans.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp).duration(300).playOn(mShareType);
    }

    private void animationOut() {
        mShareCapsule.setVisibility(View.VISIBLE);
        isShareVisible = false;
        mShareTrans.setVisibility(View.GONE);
        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(mShareType);
    }

    private Platform.ShareParams shareParams() {
        String imagePath = ShareUtil.getCapsuleImgPath(currentTime);

        Platform.ShareParams wechat = new Platform.ShareParams();
        wechat.setImagePath(imagePath);
        wechat.setShareType(Platform.SHARE_IMAGE);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
       if (v.getId() == R.id.share_trans) {
            int y = (int) event.getY();
            int top = mShareType.getTop();
            if (y < top && isShareVisible) animationOut();
            return false;
        }
        return false;
    }
}

