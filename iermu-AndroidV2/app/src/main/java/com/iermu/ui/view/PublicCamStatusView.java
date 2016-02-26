package com.iermu.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cms.media.widget.VideoView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.util.Logger;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

/**
 * Created by zhangxq on 15/8/18.
 */
public class PublicCamStatusView extends RelativeLayout implements View.OnClickListener {
    public static final int STATUS_LOADING = 0; // 加载中
    public static final int STATUS_ERROR = 1; // 加载出错
    public static final int STATUS_CAM_OFF = 2; // 摄像头关闭
    public static final int STATUS_ERROR_WITH_SUB_TEXT = 3; // 加载出错，带副说明
    public static final int STATUS_CAM_OFF_WITH_SUB_TEXT = 4; // 摄像头关闭，带副说明
    public static final int STATUS_RESOLVE = 5; // 查看如何解决
    public static final int STATUS_NOT_WIFI = 6; // 正在使用3g或4g
    public static final int STATUS_DEV_START = 7; //开机

    @ViewInject(R.id.viewStatusCenter)
    View viewStatus;
    @ViewInject(R.id.viewLoading)
    View viewLoading;
    @ViewInject(R.id.imageViewLoading)
    ImageView imageViewLoading;
    @ViewInject(R.id.imageViewRefresh)
    ImageView imageViewRefresh;
    @ViewInject(R.id.imageViewCamOff)
    ImageView imageViewCamOff;
    @ViewInject(R.id.buttonResolve)
    Button buttonResolve;
    @ViewInject(R.id.viewNotWifi)
    View viewNotWifi;
    @ViewInject(R.id.imageViewClose)
    ImageView imageViewClose;
    @ViewInject(R.id.buttonStart)
    Button buttonStart;
    @ViewInject(R.id.textViewStatus)
    TextView textViewStatus;
    @ViewInject(R.id.textViewStatusTips)
    TextView textViewStatusTips;

    @ViewInject(R.id.viewSwitch)
    View viewSwitch;
    @ViewInject(R.id.buttonSwitchNow)
    Button buttonSwitchNow;
    @ViewInject(R.id.imageViewSwitchClose)
    ImageView imageViewSwitchClose;


    private Animation animation;

    private Context context;
    private PublicStatusListener listener;

    public PublicCamStatusView(Context context) {
        super(context);
    }

    public PublicCamStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_pub_cam_status, this, true);
        ViewHelper.inject(this, view);

        imageViewRefresh.setOnClickListener(this);
        buttonResolve.setOnClickListener(this);
        imageViewClose.setOnClickListener(this);
        buttonStart.setOnClickListener(this);
        buttonSwitchNow.setOnClickListener(this);
        imageViewSwitchClose.setOnClickListener(this);
        // 加载时动画
        animation = AnimationUtils.loadAnimation(context, R.anim.rotate_load);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);

        imageViewLoading.startAnimation(animation);
    }


    /**
     * 是否显示
     *
     * @return
     */
    public boolean isVisible() {
        return getVisibility() == View.VISIBLE;
    }

    /**
     * 设置状态提示
     *
     * @param text
     */
    public void setStatusText(String text) {
        textViewStatus.setText(text);
    }

    /**
     * 设置状态副提示
     *
     * @param text
     */
    public void setStatusTextTips(String text) {
        textViewStatusTips.setText(text);
    }

    /**
     * 设置按钮监听
     *
     * @param listener
     */
    public void setListener(PublicStatusListener listener) {
        this.listener = listener;
    }

    /**
     * 隐藏状态view
     */
    public void hideViewStatus() {
        viewStatus.setVisibility(View.GONE);
    }

    /**
     * 显示状态view
     */
    public void showViewStatus(int status, String imageCache, String title, String subTitle) {
        // 切换状态时初始化所有控件
        viewStatus.setVisibility(View.VISIBLE);
        viewLoading.setVisibility(View.GONE);
        imageViewRefresh.setVisibility(View.GONE);
        imageViewCamOff.setVisibility(View.GONE);
        textViewStatusTips.setVisibility(View.GONE);
        buttonResolve.setVisibility(View.GONE);
        buttonStart.setVisibility(View.GONE);

        // 设置提示内容
        if (title != null && title.length() > 0) {
            textViewStatus.setText(title);
        }

        if (subTitle != null && subTitle.length() > 0) {
            textViewStatusTips.setText(subTitle);
        }

        switch (status) {
            case STATUS_LOADING:
                viewLoading.setVisibility(View.VISIBLE);
                break;
            case STATUS_ERROR:
                imageViewRefresh.setVisibility(View.VISIBLE);
                break;
            case STATUS_CAM_OFF:
                imageViewCamOff.setVisibility(View.VISIBLE);
                break;
            case STATUS_ERROR_WITH_SUB_TEXT:
                imageViewRefresh.setVisibility(View.VISIBLE);
                textViewStatusTips.setVisibility(View.VISIBLE);
                break;
            case STATUS_CAM_OFF_WITH_SUB_TEXT:
                imageViewCamOff.setVisibility(View.VISIBLE);
                textViewStatusTips.setVisibility(View.VISIBLE);
                break;
            case STATUS_RESOLVE:
                buttonResolve.setVisibility(View.VISIBLE);
                break;
            case STATUS_DEV_START:
                buttonStart.setVisibility(View.VISIBLE);
                textViewStatusTips.setVisibility(View.VISIBLE);
            default:
                break;
        }
    }

    /**
     * 获取viewstatus的显示状态
     *
     * @return
     */
    public boolean isViewStatusShow() {
        if (viewStatus.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 隐藏非wifi提示
     */
    public void hideViewNotWifi() {
        viewNotWifi.setVisibility(View.GONE);
    }

    /**
     * 显示非wifi提示
     */
    public void showViewNotWifi() {
        viewNotWifi.setVisibility(View.VISIBLE);
    }

    /**
     * 显示立即切换码流提示
     */
    public void showViewSwitch() {
        viewNotWifi.setVisibility(View.GONE);
        viewSwitch.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏立即切换码流提示
     */
    public void hideViewSwitch() {
        viewSwitch.setVisibility(View.GONE);
        if (listener != null) {
            listener.onSwitchViewHide(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewRefresh:
                if (listener != null) {
                    listener.onRefreshClick(this);
                }
                break;
            case R.id.buttonResolve:
                if (listener != null) {
                    listener.onButtonResolveClick(this);
                }
                break;
            case R.id.imageViewClose:
                hideViewNotWifi();
            case R.id.buttonStart:
                if (listener != null) {
                    listener.onButtonStartClick(this);
                }
                break;
            case R.id.buttonSwitchNow:
                hideViewSwitch();
                if (listener != null) {
                    listener.onSwitchClick(this);
                }
                break;
            case R.id.imageViewSwitchClose:
                hideViewSwitch();
                break;
            default:
                break;
        }
    }

    /**
     * 按钮监听回调
     */
    public interface PublicStatusListener {
        /**
         * 当刷新按钮点击时
         *
         * @param view
         */
        void onRefreshClick(PublicCamStatusView view);

        /**
         * 当查看如何解决按钮点击时
         *
         * @param view
         */
        void onButtonResolveClick(PublicCamStatusView view);

        /**
         * 开机按钮点击时
         *
         * @param view
         */
        void onButtonStartClick(PublicCamStatusView view);

        /**
         * 立即切换按钮点击时
         *
         * @param view
         */
        void onSwitchClick(PublicCamStatusView view);

        /**
         * 立即切换view隐藏时调用
         *
         * @param view
         */
        void onSwitchViewHide(PublicCamStatusView view);
    }
}
