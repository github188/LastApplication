package com.iermu.ui.fragment.MineIermu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.view.ShareImgpathDialog;
import com.mob.tools.utils.UIHandler;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 分享截屏大图展示页面
 * <p/>
 * Created by zhangxq on 15/10/14.
 */
public class ShareImageFragment extends BaseFragment implements PlatformActionListener, Handler.Callback, View.OnTouchListener {
    public static final String INTENT_SAVE_IMAGE_COMPLETE = "save_image_complete";
    public static final String KEY_DEVICE_NAME = "deviceName";
    public static final String KEY_TIME = "time";
    private static final int MSG_ACTION_CCALLBACK = 2;

    @ViewInject(R.id.textViewTitle)
    TextView textViewTitle;
    @ViewInject(R.id.textViewTime)
    TextView textViewTime;
    @ViewInject(R.id.imageViewCut)
    ImageView imageViewCut;
    @ViewInject(R.id.share_layout)
    RelativeLayout shareLayout;
    @ViewInject(R.id.buttonShare)
    TextView buttonShare;
    @ViewInject(R.id.share_trans)
    View mShareTrans;
    @ViewInject(R.id.share_wechat)
    Button mShareWx;
    @ViewInject(R.id.share_moments)
    Button mShareMoments;
    @ViewInject(R.id.share_type)
    RelativeLayout mShareType;

    private String imagePath;
    private long time;
    private Bitmap bitmap;
    private ShareImgpathDialog shareDialog;
    private boolean isShareVisible = false;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buttonShare.setEnabled(true);
        }
    };

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_single_img);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    private static boolean isOpened;

    public static Fragment actionInstance(String deviceName, long time) {
        if (!isOpened) {
            isOpened = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOpened = false;
                }
            }, 1000);

            ShareImageFragment fragment = new ShareImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_DEVICE_NAME, deviceName);
            bundle.putLong(KEY_TIME, time);
            fragment.setArguments(bundle);
            return fragment;
        } else {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_share_image, null);
        ViewHelper.inject(this, view);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        mShareTrans.setOnTouchListener(this);
        String deviceName = getArguments().getString(KEY_DEVICE_NAME);
        time = (Long) getArguments().get(KEY_TIME);
        textViewTitle.setText(deviceName);
        textViewTime.setText(DateUtil.formatDate(new Date(time), DateUtil.FORMAT_ONE));
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(INTENT_SAVE_IMAGE_COMPLETE));

        bitmap = ShareUtil.getBitmapShare().copy(Bitmap.Config.ARGB_8888, true);
        imageViewCut.setImageBitmap(bitmap);

        return view;
    }

    @OnClick(value = {R.id.buttonShare, R.id.actionbar_back, R.id.share_wechat, R.id.share_moments, R.id.share_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonShare://TODO 分享自定义布局
                buttonShare.setEnabled(false);
//                ShareUtil.share(getActivity(), ShareUtil.getImagePath(time));
                imagePath = ShareUtil.getImagePath(time);
                if (!TextUtils.isEmpty(imagePath)) aniIn();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonShare.setEnabled(true);
                    }
                }, 200);
                break;
            case R.id.actionbar_back:
                popBackStack();
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
            if (isShareVisible) {
                animationOut();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        getActivity().unregisterReceiver(mBroadcastReceiver);
        bitmap.recycle();
        super.onDestroyView();
    }

    /**
     * 设置图片
     */
    private void setImage() {
        imagePath = ShareUtil.getImagePath(time);
        Logger.d("imagePath:" + imagePath);
        Picasso.with(getActivity()).load(new File(imagePath)).into(imageViewCut);
    }

    private void animationIn() {
        shareDialog = new ShareImgpathDialog(getActivity(), R.style.custom_dialog, imagePath);
        Window window = shareDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.dialogAnim_style);  //添加动画
        window.setBackgroundDrawableResource(R.drawable.custom_bg);
        shareDialog.show();
    }

    private void aniIn() {
        buttonShare.setVisibility(View.INVISIBLE);
        isShareVisible = true;
        mShareType.setVisibility(View.VISIBLE);
        mShareTrans.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp).duration(300).playOn(mShareType);
    }

    private void animationOut() {
        buttonShare.setVisibility(View.VISIBLE);
        isShareVisible = false;
        mShareTrans.setVisibility(View.GONE);
        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(mShareType);
    }

    private Platform.ShareParams shareParams() {
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
