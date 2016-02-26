package com.iermu.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cms.media.widget.VideoView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.config.PathConfig;
import com.iermu.client.util.Logger;
import com.iermu.ui.util.CapsuleUtil;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.util.Util;
import com.nineoldandroids.animation.Animator;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangxq on 15/7/21.
 */
public class MainLiveControllerView extends RelativeLayout implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private Context context;
    @ViewInject(R.id.textViewControllerTitle)
    TextView textViewControllerTitle;
    @ViewInject(R.id.viewLanscapeControl)
    View viewLanscapeControl;
    @ViewInject(R.id.buttonCloudCtrl)
    ImageButton buttonCloudCtrl;
    @ViewInject(R.id.buttonLevelCtrl)
    ImageButton buttonLevelCtrl;
    @ViewInject(R.id.buttonOutFullScreen)
    ImageButton buttonOutFullScreen;
    @ViewInject(R.id.buttonVoice)
    ImageButton buttonVoice;
    @ViewInject(R.id.radioGroup)
    RadioGroup radioGroup;
    @ViewInject(R.id.radioButtonLevel1)
    RadioButton radioButtonLevel1;
    @ViewInject(R.id.radioButtonLevel2)
    RadioButton radioButtonLevel2;
    @ViewInject(R.id.radioButtonLevel3)
    RadioButton radioButtonLevel3;
    @ViewInject(R.id.viewStatus)
    PublicCamStatusView viewStatus;
    @ViewInject(R.id.imageButtonSpeakCtrl)
    TouchEventImageButton imageButtonSpeak;
    @ViewInject(R.id.imageButtonPrintScreenCtrl)
    ImageButton imageButtonPrintScreen;
    @ViewInject(R.id.imageButtonRecordCtrl)
    ImageButton imageButtonRecord;
    @ViewInject(R.id.viewCut)
    View viewCut;
    @ViewInject(R.id.imageViewCut)
    ImageView imageViewCut;
    @ViewInject(R.id.textViewCut)
    TextView textViewCut;
    @ViewInject(R.id.linearLayoutTop)
    View linearLayoutTop;
    @ViewInject(R.id.linearLayoutRight)
    View linearLayoutRight;
    @ViewInject(R.id.linearLayoutLeft)
    View linearLayoutLeft;
    @ViewInject(R.id.viewIndicator)
    View viewIndicator;
    @ViewInject(R.id.imageViewIndicatorBar)
    ImageView imageViewIndicatorBar;
    @ViewInject(R.id.imageViewIndicatorCircle)
    ImageView imageViewIndicatorCircle;
    @ViewInject(R.id.textViewToast)
    TextView textViewToast;
    @ViewInject(R.id.imageviewStrengthCtrl)
    ImageView imageviewStrengthCtrl;
    @ViewInject(R.id.viewSpeed)
    RelativeLayout viewSpeed;

    // 空气胶囊
    @ViewInject(R.id.capsule_lay)
    RelativeLayout mCapsuleLay;
    @ViewInject(R.id.viewTemperature)
    RelativeLayout viewTemperature;
    @ViewInject(R.id.anim_capsule_tem)
    RelativeLayout mAnimCapsuleTem;
    @ViewInject(R.id.anim_capsule_hum)
    RelativeLayout mAnimCapsuleHum;
    @ViewInject(R.id.temperature_txt)
    TextView mTemperatureTxt;
    @ViewInject(R.id.temperature_img)
    ImageView mTemperatureImg;
    @ViewInject(R.id.humidity_txt)
    TextView mHumidityTxt;
    @ViewInject(R.id.humidity_img)
    ImageView mHumidityImg;


    @ViewInject(R.id.view_capsule)
    LinearLayout mViewCapsule;
    @ViewInject(R.id.temperature)
    TextView mTemperature;
    @ViewInject(R.id.humidity)
    TextView mHumidity;
    @ViewInject(R.id.temperature_level)
    TextView mTemperatureLevel;
    @ViewInject(R.id.humidity_level)
    TextView mHumidityLevel;
    @ViewInject(R.id.check_details)
    Button mCheckDetails;
    @ViewInject(R.id.img_temperature)
    ImageView mTemImg;
    @ViewInject(R.id.img_humidity)
    ImageView mHumImg;


    // 云台有关控件
    @ViewInject(R.id.viewCloudTip)
    View viewCloudTip;
    @ViewInject(R.id.textViewCloudTip)
    TextView textViewCloudTip;
    @ViewInject(R.id.imageButtonCloseTip)
    ImageButton imageButtonCloseTip;
    @ViewInject(R.id.textViewMostLeft)
    TextView textViewMostLeft;
    @ViewInject(R.id.textViewMostRight)
    TextView textViewMostRight;
    @ViewInject(R.id.imageViewPress)
    ImageView imageViewPress;
    @ViewInject(R.id.speed_text)
    TextView speedText;
    @ViewInject(R.id.imageViewAnimateTop)
    ImageView imageViewAnimateTop;
    @ViewInject(R.id.viewCenterAnimate)
    View viewCenterAnimate;
    @ViewInject(R.id.imageButtonCtrlCloudAuto)
    ImageButton imageButtonCtrlCloudAuto;

    private boolean isLandscape = false;
    private boolean isShowController = false;
    private boolean isVoiceOn = true;
    private boolean isLevelShow = false;
    private boolean isSpeaking = false;
    private boolean isIndicatorShow = false;

    private VideoView viewVideo;
    private View viewTop; // 视频区域view，包含控制器
    private int height; // 计算出来的控制器高度

    // 画面移动有关参数
    private int marginLeft;
    private int touchX;
    private int offsetx;

    // 广角指示器移动有关参数
    private int indicatorWidth = 0;
    private int circleOffsetx;

    // 照相声音播放
    MediaPlayer mediaPlayer;

    // 云台有关参数
    private long touchTime = 0; // 记录上一次触摸时间，和下一次的时间比较，判断是否是双击
    private float positionx; // 记录双击位置x
    private float positiony; // 记录双击位置y
    private boolean isDoubleClick; // 记录是否是双击
    private double angleOld;
    private boolean isIncloud; // 记录是否处于云台状态
    private boolean isAutoStart; // 记录云台是否处于自动旋转

    //温湿度
    private int temperature;
    private int humidity;
    private AlertDialog capsuleDialog;
    private static final int UPDATE = 0;
    private Handler handler;
    private boolean animateFirst = true;
    private Timer temTimer;

    private OnControlListener listener;

    public void setViewTop(View viewTop) {
        this.viewTop = viewTop;
    }

    public void setVideoHeight(int height) {
        this.height = height;
    }

    public void setbitlevel(int level, boolean isDoNetWork) {
        switch (level) {
            case 0:
                radioGroup.check(R.id.radioButtonLevel1);
                break;
            case 1:
                radioGroup.check(R.id.radioButtonLevel2);
                break;
            case 2:
                radioGroup.check(R.id.radioButtonLevel3);
                break;
        }
        if (listener != null) {
            listener.onLevelClick(this, level, isDoNetWork);
        }
    }

    public void setViewVideo(VideoView viewVideo) {
        this.viewVideo = viewVideo;
        PublicCamStatusView view = getViewStatus();
    }

    public void setSpeakTouchListener(OnTouchListener listener) {
        imageButtonSpeak.setOnTouchListener(listener);
    }


    public void setOnControlListener(OnControlListener listener) {
        this.listener = listener;
    }

    public MainLiveControllerView(Context context) {
        super(context);
    }

    public MainLiveControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_main_live_controller, this);
        ViewHelper.inject(this, view);

        buttonOutFullScreen.setOnClickListener(this);
        buttonVoice.setOnClickListener(this);
        buttonCloudCtrl.setOnClickListener(this);
        buttonLevelCtrl.setOnClickListener(this);
        viewCut.setOnClickListener(this);
        imageButtonPrintScreen.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        imageButtonRecord.setOnClickListener(this);
        radioGroup.check(R.id.radioButtonLevel1);
        radioButtonLevel1.setOnClickListener(this);
        radioButtonLevel2.setOnClickListener(this);
        radioButtonLevel3.setOnClickListener(this);
        imageButtonCloseTip.setOnClickListener(this);
        viewTemperature.setOnClickListener(this);
        mCheckDetails.setOnClickListener(this);
        imageButtonCtrlCloudAuto.setOnClickListener(this);
        mediaPlayer = MediaPlayer.create(context, R.raw.music_image_cut_);
    }

    public PublicCamStatusView getViewStatus() {
        return viewStatus;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCloudCtrl:
                if (isIncloud) {
                    if (isAutoStart) {
                        buttonCloudCtrl.setImageResource(R.drawable.main_live_ctrl_cloud_autostart);
                    } else {
                        buttonCloudCtrl.setImageResource(R.drawable.main_live_ctrl_cloud);
                    }
                    linearLayoutLeft.setVisibility(View.INVISIBLE);
                } else {
                    buttonCloudCtrl.setImageResource(R.drawable.main_live_ctrl_cloud_closeup);
                    linearLayoutLeft.setVisibility(View.VISIBLE);
                }
                if (listener != null) {
                    listener.onButtonCloudClick();
                }
                isIncloud = !isIncloud;
                delayHideCtrl();
                break;
            case R.id.buttonLevelCtrl:
                if (isLevelShow) {
                    radioGroup.setVisibility(View.GONE);
                } else {
                    radioGroup.setVisibility(View.VISIBLE);
                }
                isLevelShow = !isLevelShow;
                delayHideCtrl();
                break;
            case R.id.buttonVoice:
                changeVoiceState();
                if (listener != null) {
                    listener.onSoundClick(this);
                }
                delayHideCtrl();
                break;
            case R.id.buttonOutFullScreen:
                if (listener != null) {
                    listener.onOutFullScreenClick(this);
                }

                isLandscape = false;
                isShowController = false;
                break;
            case R.id.imageButtonRecordCtrl:
                if (listener != null) {
                    listener.onRecordClick(this);
                }
                delayHideCtrl();
                break;
            case R.id.viewCut:
                if (listener != null) {
                    listener.onViewCutClick(this);
                }
                viewCut.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewCut.setEnabled(true);
                    }
                }, 100);
                break;
            case R.id.imageButtonPrintScreenCtrl:
                if (listener != null) {
                    listener.onPrintScreenClick(this);
                }
                delayHideCtrl();
                break;
            case R.id.radioButtonLevel1:
                if (listener != null) {
                    listener.onLevelClick(this, 0, true);
                }
                break;
            case R.id.radioButtonLevel2:
                if (listener != null) {
                    listener.onLevelClick(this, 1, true);
                }
                break;
            case R.id.radioButtonLevel3:
                if (listener != null) {
                    listener.onLevelClick(this, 2, true);
                }
                break;
            case R.id.imageButtonCloseTip:
                viewCloudTip.setVisibility(View.GONE);
                break;
            case R.id.viewTemperature:
                viewTemperature.setVisibility(View.GONE);
                mViewCapsule.setVisibility(View.VISIBLE);
                break;
            case R.id.check_details:
                if (listener != null) {
                    listener.onActionCapsuleFragment(temperature, humidity);
                    mCheckDetails.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCheckDetails.setEnabled(true);
                        }
                    }, 300);
                }
                break;
            case R.id.imageButtonCtrlCloudAuto:
                if (isAutoStart) {
                    imageButtonCtrlCloudAuto.setImageResource(R.drawable.main_live_ctrl_cloud_auto);
                } else {
                    imageButtonCtrlCloudAuto.setImageResource(R.drawable.main_live_ctrl_cloud_stop);
                }
                isAutoStart = !isAutoStart;
                if (listener != null) {
                    listener.onButtonAutoClick();
                }
                delayHideCtrl();
                break;
            default:
                break;
        }
    }

    /**
     * 修改声音状态
     */
    public void changeVoiceState() {
        if (isVoiceOn) {
            buttonVoice.setSelected(true);
        } else {
            buttonVoice.setSelected(false);
        }
        isVoiceOn = !isVoiceOn;
    }

    /**
     * 处理转横屏时的操作
     */
    public void changeToLandscape() {
        if (capsuleDialog != null && capsuleDialog.isShowing()) capsuleDialog.dismiss();
        mCapsuleLay.setVisibility(View.GONE);
        viewVideo.setTranslationX(0);
        textViewCut.setVisibility(View.GONE);
        viewIndicator.setVisibility(View.GONE);
        viewLanscapeControl.setVisibility(View.VISIBLE);
        isLandscape = true;
        isShowController = false;
        changeLanscapeControlHideState();

        // 设置播放器的布局为全屏
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewTop.setLayoutParams(layoutParams);

        // 处理radioGroup
        radioGroup.setVisibility(View.GONE);
        isLevelShow = false;
        LayoutParams layoutParamsRadioGroup = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsRadioGroup.setMargins(0, Util.DensityUtil.dip2px(context, 53), Util.DensityUtil.dip2px(context, 152), 0);
        layoutParamsRadioGroup.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        radioGroup.setLayoutParams(layoutParamsRadioGroup);

        LayoutParams paramsVideoView = (LayoutParams) viewVideo.getLayoutParams();
        paramsVideoView.setMargins(0, 0, 0, 0);

        RelativeLayout.LayoutParams params = (LayoutParams) textViewToast.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

        ViewGroup.LayoutParams paramTopView = linearLayoutTop.getLayoutParams();
        int topViewHeight = paramTopView.height;
        LayoutParams paramCloudView = (LayoutParams) viewCloudTip.getLayoutParams();
        paramCloudView.setMargins(0, topViewHeight + 20, 0, 0);

        RelativeLayout.LayoutParams speedParams = (LayoutParams) viewSpeed.getLayoutParams();
        speedParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        speedParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        viewSpeed.setLayoutParams(speedParams);
    }

    /**
     * 处理转竖屏时的操作
     */
    public void changeToPortrait() {
        if (temperature > 0 && humidity > 0) mCapsuleLay.setVisibility(View.VISIBLE);
        viewVideo.setTranslationX(offsetx);
        textViewCut.setVisibility(View.VISIBLE);
        if (isIndicatorShow) {
            viewIndicator.setVisibility(View.VISIBLE);
        }
        viewLanscapeControl.setVisibility(View.INVISIBLE);
        isLandscape = false;
        isShowController = false;

        // 设置播放器的布局，退出全屏
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, height);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewTop.setLayoutParams(layoutParams);

        // 处理radioGroup
        radioGroup.setVisibility(View.GONE);
        isLevelShow = false;
        LayoutParams layoutParamsRadioGroup = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsRadioGroup.setMargins(Util.DensityUtil.dip2px(context, 111), 0, 0, Util.DensityUtil.dip2px(context, 5));
        layoutParamsRadioGroup.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        radioGroup.setLayoutParams(layoutParamsRadioGroup);

        LayoutParams paramsVideoView1 = (LayoutParams) viewVideo.getLayoutParams();
        paramsVideoView1.setMargins(-marginLeft, 0, -marginLeft, 0);

        RelativeLayout.LayoutParams params = (LayoutParams) textViewToast.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, 0);

        LayoutParams paramCloudView = (LayoutParams) viewCloudTip.getLayoutParams();
        paramCloudView.setMargins(0, 30, 0, 0);

        RelativeLayout.LayoutParams speedParams = (LayoutParams) viewSpeed.getLayoutParams();
        speedParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        speedParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        viewSpeed.setLayoutParams(speedParams);
    }

    /**
     * 设置控制器标题
     *
     * @param title
     */
    public void setTitle(String title) {
        textViewControllerTitle.setText(title);
    }

    public boolean isVoiceOn() {
        return isVoiceOn;
    }

    public void setVoiceOn(boolean isVoiceOn) {
        this.isVoiceOn = isVoiceOn;
        buttonVoice.setSelected(!isVoiceOn);
    }

    /**
     * 设置声音按键的禁用状态
     *
     * @param enable
     */
    public void setVoiceEnable(boolean enable) {
        buttonVoice.setEnabled(enable);
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    /**
     * 设置是否正在讲话，如果讲话完毕，则隐藏按钮
     *
     * @param isSpeaking
     */
    public void setIsSpeaking(final boolean isSpeaking) {
        this.isSpeaking = isSpeaking;
        if (!isSpeaking) {
            delayHideCtrl();
        }
    }

    // 隐藏清晰度选择列表
    public void hideLevel() {
        radioGroup.setVisibility(View.GONE);
        isLevelShow = false;
    }

    // 设置云台提示的隐藏状态
    public void setViewCloudTipVisible(boolean ishide) {
        if (ishide) {
            viewCloudTip.setVisibility(View.GONE);
        } else {
            viewCloudTip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置是否处于云台状态
     *
     * @param isInCloud
     */
    public void setIsInCloud(boolean isInCloud) {
        this.isIncloud = isInCloud;
        if (isIncloud) {
            buttonCloudCtrl.setImageResource(R.drawable.main_live_ctrl_cloud_closeup);
            linearLayoutLeft.setVisibility(View.VISIBLE);
        } else {
            if (isAutoStart) {
                buttonCloudCtrl.setImageResource(R.drawable.main_live_ctrl_cloud_autostart);
            } else {
                buttonCloudCtrl.setImageResource(R.drawable.main_live_ctrl_cloud);
            }
            linearLayoutLeft.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取云台状态
     *
     * @return
     */
    public boolean isInCloud() {
        return isIncloud;
    }

    /**
     * 设置是否开启平扫
     *
     * @param isStartAuto
     */
    public void setIsStartAuto(boolean isStartAuto) {
        this.isAutoStart = isStartAuto;
        if (isAutoStart) {
            imageButtonCtrlCloudAuto.setImageResource(R.drawable.main_live_ctrl_cloud_stop);
        } else {
            imageButtonCtrlCloudAuto.setImageResource(R.drawable.main_live_ctrl_cloud_auto);
        }
    }

    /**
     * 获取是否开启平扫
     *
     * @return
     */
    public boolean isAutoStart() {
        return isAutoStart;
    }

    /**
     * 设置广角指示器的显示或隐藏
     *
     * @param isShow
     */
    public void setIsIndicatorShow(boolean isShow) {
        isIndicatorShow = isShow;
        if (isShow) {
            if (!isLandscape) {
                viewIndicator.setVisibility(View.VISIBLE);
            }
        } else {
            viewIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * 设置全屏切换按钮的禁用
     *
     * @param isEnable
     */
    public void setButtonOutFullScreenEnable(boolean isEnable) {
        buttonOutFullScreen.setEnabled(isEnable);
    }

    /**
     * 设置云台提示
     *
     * @param tipText
     */
    public void setCloudTipText(String tipText) {
        textViewCloudTip.setText(tipText);
    }

    /**
     * 显示云台提示
     */
    public void showCloudTip() {
        viewCloudTip.setVisibility(View.VISIBLE);
    }

    /**
     * 展示已经到最右边动画
     */
    public void animateMostRight() {
        YoYo.with(Techniques.SlideInLeft).duration(1000).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                textViewMostLeft.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                YoYo.with(Techniques.SlideOutLeft).delay(1000).duration(2000).playOn(textViewMostLeft);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(textViewMostLeft);
    }

    /**
     * 展示已经到最左边动画
     */
    public void animateMostLeft() {
        YoYo.with(Techniques.SlideInRight).duration(1000).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                textViewMostRight.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                YoYo.with(Techniques.SlideOutRight).delay(1000).duration(2000).playOn(textViewMostRight);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(textViewMostRight);
    }

    /**
     * 设置语音音量图片
     *
     * @param imageId
     */
    public void setAudioStrengthImage(int imageId) {
        imageviewStrengthCtrl.setImageResource(imageId);
    }

    /**
     * 设置音量提示隐藏状态
     *
     * @param isShow
     */
    public void setAudioStrengthImageShow(boolean isShow) {
        if (isShow) {
            imageviewStrengthCtrl.setVisibility(View.VISIBLE);
        } else {
            imageviewStrengthCtrl.setVisibility(View.GONE);
        }
    }

    /**
     * 设置视频网速显示状态
     *
     * @param isShow
     */
    public void setSpeedTextShow(boolean isShow) {
        if (isShow) {
            viewSpeed.setVisibility(View.VISIBLE);
        } else {
            viewSpeed.setVisibility(View.GONE);
        }
    }

    /**
     * 设置空气胶囊的显示和隐藏
     *
     * @param isShow
     */
    public void setCapsuleLayInvisible(boolean isShow) {
        if (isShow) mCapsuleLay.setVisibility(View.GONE);
    }

    /**
     * 取消温度循环定时
     */
    public void timerCancel() {
        if (temTimer != null) temTimer.cancel();
    }

    /**
     * 无码流不能点击查看详情
     *
     * @param isShow
     */
    public void setCheckDetailShow(boolean isShow) {
        if (isShow) {
            mCheckDetails.setEnabled(true);
        } else {
            viewSpeed.setEnabled(false);
        }
    }

    /**
     * 隐藏网速悬框
     */
    public void hideSpeed() {
        viewSpeed.setVisibility(View.GONE);
    }

    /**
     * 显示网速悬框
     */
    public void displaySpeed() {
        if (viewCut.getVisibility() == VISIBLE || textViewToast.getVisibility() == VISIBLE) {
            viewSpeed.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 移动网速视图
     *
     * @param isToUp
     */
    public void speedMove(boolean isToUp) {
        int offect = 0;
        if (isToUp) {
            offect = -Util.DensityUtil.dip2px(context, 40);
        }
        viewSpeed.setTranslationY(offect);
    }

    private int delayNum = 0;

    public void changeLanscapeControlHideState() {
        if (isLandscape) {
            if (isShowController) {
                hideLanscapeControl();
            } else {
                showLanscapeControl();
                delayHideCtrl();
            }
            isShowController = !isShowController;
        }
    }

    private void delayHideCtrl() {
        delayNum++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 过3秒如果没有隐藏，则自动隐藏
                delayNum--;
                if (delayNum == 0 && !isSpeaking && !isLevelShow) {
                    hideLanscapeControl();
                    isShowController = false;
                }
            }
        }, 3000);
    }

    /**
     * 显示或隐藏清晰度选择
     */
    public void changeRadioGroupState() {
        if (isLevelShow) {
            radioGroup.setVisibility(View.GONE);
        } else {
            radioGroup.setVisibility(View.VISIBLE);
        }
        isLevelShow = !isLevelShow;
    }

    /**
     * 截图
     */
    public void printScreen(boolean isPlayMusic) {
        if (isPlayMusic) {
            mediaPlayer.start();
        }
        final Bitmap bitmap = viewVideo.takePicture();
        if (bitmap == null) {
            return;
        }
        Bitmap smallBitMap = ShareUtil.compressImage(bitmap, 20);
        final long imageTime = new Date().getTime();
        if (listener != null) {
            listener.onPrintScreenComplete(MainLiveControllerView.this, smallBitMap, imageTime);
        }
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onSaveScreenImageComplete(MainLiveControllerView.this);
                }
            }
        });
    }

    /**
     * 显示截图
     *
     * @param bitmap
     */
    private int cutImageNum = 0;

    public void setImage(Bitmap bitmap) {
        viewCut.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceIn).duration(300).playOn(viewCut);
        cutImageNum++;
        imageViewCut.setImageBitmap(bitmap);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                cutImageNum--;
                if (cutImageNum == 0) {
                    viewCut.setVisibility(View.GONE);
                    ShareUtil.getBitmapShare().recycle();
                }
            }
        }, 2500);
    }

    /**
     * 显示截屏保存toast
     */
    private int cutNum = 0;

    public void imageCutToast() {
        cutNum++;
        Logger.d("cutNum:" + cutNum);
        textViewToast.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                cutNum--;
                if (cutNum == 0) {
                    toast(getResources().getString(R.string.save_screen));//"截屏已保存到" + PathConfig.CACHE_SHARE
                }
            }
        }, 3500);
    }

    /**
     * 获取网速
     */
    public void getSpeed(String txt) {
        if (speedText != null) speedText.setText(txt + "KB");
    }

    /**
     * 空气胶囊温度
     *
     * @param deviceId
     * @param tem
     * @param hum
     */
    public void setCapsule(String deviceId, int tem, int hum) {
        temperature = tem;
        humidity = hum;
        if (isLandscape) {
            mCapsuleLay.setVisibility(View.GONE);
        } else {
            mCapsuleLay.setVisibility(View.VISIBLE);
        }
        boolean dialogShow = ErmuBusiness.getPreferenceBusiness().isCapsuleDialogShow(deviceId);
        if (dialogShow) showCapsuleDialog(deviceId);
        int temNum = CapsuleUtil.temNum(temperature);
        int humNum = CapsuleUtil.humNum(humidity);
        mTemperatureTxt.setText(temperature + "°C");
        mTemperature.setText(temperature + "°C");
        mHumidity.setText(humidity + "%");

        Resources resources = context.getResources();
        String[] temArray = resources.getStringArray(R.array.temperature_level);
        String[] humArray = resources.getStringArray(R.array.humidity_level);
        mTemperatureLevel.setText(temArray[temNum - 1]);
        mHumidityLevel.setText(humArray[humNum - 1]);

        if (humNum == 3) {
            mHumImg.setBackgroundResource(R.drawable.humidity_green);
        } else if (humNum == 1 || humNum == 4) {
            mHumImg.setBackgroundResource(R.drawable.humidity_red);
        } else if (humNum == 2) {
            mHumImg.setBackgroundResource(R.drawable.humidity_yellow);
        }
        if (temNum == 5) {
            mTemImg.setBackgroundResource(R.drawable.temperature_green);
            mTemperatureImg.setBackgroundResource(R.drawable.temperature_green);
        } else if (temNum == 1 || temNum == 2 || temNum == 8 || temNum == 9 || temNum == 10) {
            mTemImg.setBackgroundResource(R.drawable.temperature_red);
            mTemperatureImg.setBackgroundResource(R.drawable.temperature_red);
        } else if (temNum == 3 || temNum == 4 || temNum == 6 || temNum == 7) {
            mTemImg.setBackgroundResource(R.drawable.temperature_yellow);
            mTemperatureImg.setBackgroundResource(R.drawable.temperature_yellow);
        }
        if (temTimer == null) capsuleAni(temperature, humidity);
    }

    //空气胶囊dialog
    private void showCapsuleDialog(String deviceId) {
        ErmuBusiness.getPreferenceBusiness().setCapsuleDialogShow(deviceId);
        View view = View.inflate(getContext(), R.layout.capsule_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        capsuleDialog = builder.create();
        capsuleDialog.show();
        WindowManager.LayoutParams params = capsuleDialog.getWindow().getAttributes();
        params.width = DimenUtils.dip2px(context, 270);
        capsuleDialog.getWindow().setAttributes(params);
        Button mCheck = (Button) view.findViewById(R.id.check_now);
        mCheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                capsuleDialog.dismiss();
                viewTemperature.setVisibility(View.GONE);
                mViewCapsule.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewTemperature.setVisibility(View.VISIBLE);
                        mViewCapsule.setVisibility(View.GONE);
                    }
                }, 5000);
            }
        });
    }

    //温湿度切换动画
    private void capsuleAni(final int temperature, final int humidity) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == UPDATE) {
                    if (animateFirst) {
                        animHumIn(); //湿度进
                        animTemOut(); //温度出
                    } else {
                        animTemIn();//温度进
                        animHumOut(); //湿度出
                    }
                    animateFirst = !animateFirst;
                }
            }
        };

        temTimer = new Timer();
        temTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = UPDATE;
                handler.sendMessage(msg);
            }
        }, 2000, 2000);

    }

    private void animHumIn() {
        final int humNum = CapsuleUtil.humNum(humidity);
        YoYo.with(Techniques.SlideInUp).duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mHumidityTxt.setText(humidity + "%");
                if (humNum == 3) {
                    mHumidityImg.setBackgroundResource(R.drawable.humidity_green);
                } else if (humNum == 1 || humNum == 4) {
                    mHumidityImg.setBackgroundResource(R.drawable.humidity_red);
                } else if (humNum == 2) {
                    mHumidityImg.setBackgroundResource(R.drawable.humidity_yellow);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        }).playOn(mAnimCapsuleHum);
    }

    private void animTemOut() {
        YoYo.with(Techniques.SlideOutUp).duration(500).playOn(mAnimCapsuleTem);
    }

    private void animTemIn() {
        final int temNum = CapsuleUtil.temNum(temperature);
        YoYo.with(Techniques.SlideInUp).duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mTemperatureTxt.setText(temperature + "°C");
                if (temNum == 5) {
                    mTemperatureImg.setBackgroundResource(R.drawable.temperature_green);
                } else if (temNum == 1 || temNum == 2 || temNum == 8 || temNum == 9 || temNum == 10) {
                    mTemperatureImg.setBackgroundResource(R.drawable.temperature_red);
                } else if (temNum == 3 || temNum == 4 || temNum == 6 || temNum == 7) {
                    mTemperatureImg.setBackgroundResource(R.drawable.temperature_yellow);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        }).playOn(mAnimCapsuleTem);
    }

    private void animHumOut() {
        YoYo.with(Techniques.SlideOutUp).duration(500).playOn(mAnimCapsuleHum);
    }

    /**
     * 设置云台按钮的禁用
     *
     * @param isEnabled
     */
    public void setButtonCloudEnabled(boolean isEnabled) {
        buttonCloudCtrl.setEnabled(isEnabled);
        imageButtonCtrlCloudAuto.setEnabled(isEnabled);
    }

    /**
     * 设置按住说话的禁用
     *
     * @param isEnabled
     */
    public void setButtonSpeakEnabled(boolean isEnabled) {
        imageButtonSpeak.setEnabled(isEnabled);
    }

    /**
     * 设置清晰度切换按钮的禁用
     *
     * @param isEnabled
     */
    public void setButtonBitLevelEnabled(boolean isEnabled) {
        buttonLevelCtrl.setEnabled(isEnabled);
    }

    /**
     * 设置看录像按钮的禁用
     */
    public void setButtonRadioEnabled(boolean isEnadble) {
        imageButtonRecord.setEnabled(isEnadble);
    }

    /**
     * 设置截屏按钮的禁用
     *
     * @param isEnabled
     */
    public void setButtonPrintScreenEnabled(boolean isEnabled) {
        imageButtonPrintScreen.setEnabled(isEnabled);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButtonLevel1:
                buttonLevelCtrl.setImageResource(R.drawable.main_live_ctrl_level1);
                break;
            case R.id.radioButtonLevel2:
                buttonLevelCtrl.setImageResource(R.drawable.main_live_ctrl_level2);
                break;
            case R.id.radioButtonLevel3:
                buttonLevelCtrl.setImageResource(R.drawable.main_live_ctrl_level3);
                break;
            default:
                break;
        }
        radioGroup.setVisibility(View.GONE);
        isLevelShow = false;
    }

    /**
     * 切换清晰度失败时，重置
     */
    public void resetLevelOnError(int resetLevel) {
        setbitlevel(resetLevel, false);
        Logger.d("resetLevel:" + resetLevel);
        if (listener != null) {
            listener.onLevelClick(this, resetLevel, false);
        }
    }

    /**
     * 动画隐藏控制器按钮
     */
    private void hideLanscapeControl() {
        YoYo.with(Techniques.SlideOutUp).duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideLevel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(linearLayoutTop);
        YoYo.with(Techniques.SlideOutRight).duration(500).playOn(linearLayoutRight);
        YoYo.with(Techniques.SlideOutLeft).duration(500).playOn(linearLayoutLeft);
    }

    /**
     * 动画显示控制按钮
     */
    private void showLanscapeControl() {
        YoYo.with(Techniques.SlideInDown).duration(500).playOn(linearLayoutTop);
        YoYo.with(Techniques.SlideInRight).duration(500).playOn(linearLayoutRight);
        YoYo.with(Techniques.SlideInLeft).duration(500).playOn(linearLayoutLeft);
    }

    /**
     * 设置左右移动空间
     */
    public void setLeftAndRightMergin(int marginLeft) {
        this.marginLeft = marginLeft;
        RelativeLayout.LayoutParams paramsVideoView = (RelativeLayout.LayoutParams) viewVideo.getLayoutParams();
        paramsVideoView.setMargins(-marginLeft, 0, -marginLeft, 0);

        // 计算广角指示器宽度;
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        imageViewIndicatorBar.measure(w, h);
        indicatorWidth = imageViewIndicatorBar.getMeasuredWidth();
    }

    /**
     * 显示双击位置
     */
    private int doubleTouchNum; // 双击信号量

    public void showTouchPosition() {
        imageViewPress.setTranslationX(positionx);
        imageViewPress.setTranslationY(positiony);
        imageViewPress.setVisibility(View.VISIBLE);
        doubleTouchNum++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleTouchNum--;
                if (doubleTouchNum == 0) {
                    imageViewPress.setVisibility(View.GONE);
                }
            }
        }, 500);
    }

    /**
     * 中间旋转动画
     *
     * @param xCenter
     * @param yCenter
     */
    private int animateNum; // 旋转动画信号量

    public void animateCenter(int xCenter, int yCenter) {
        viewCenterAnimate.setVisibility(View.VISIBLE);
        int measuredHeight = imageViewAnimateTop.getMeasuredHeight();
        int measuredWidth = imageViewAnimateTop.getMeasuredWidth();
        int xEnd = (int) positionx;
        int yEnd = (int) positiony;
        double tanA = Math.atan2(Math.abs(xEnd - xCenter), Math.abs(yCenter - yEnd));
        double angleA = tanA * 180 / Math.PI;
        if ((xEnd - xCenter > 0) & (yCenter - yEnd > 0)) {  //1

        } else if ((xEnd - xCenter > 0) & (yCenter - yEnd < 0)) {//	4
            angleA = 180 - angleA;
        } else if ((xEnd - xCenter < 0) & (yCenter - yEnd > 0)) {//	2
            angleA = -angleA;
        } else if ((xEnd - xCenter < 0) & (yCenter - yEnd < 0)) {//	3
            angleA = -(180 - angleA);
        }

        RotateAnimation rotateAnimation = new RotateAnimation((float) angleOld, (float) angleA, measuredWidth / 2, measuredHeight / 2);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setFillAfter(true);
        imageViewAnimateTop.startAnimation(rotateAnimation);
        angleOld = angleA;

        animateNum++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateNum--;
                if (animateNum == 0) {
                    viewCenterAnimate.setVisibility(View.INVISIBLE);
                }
            }
        }, 3000);
    }

    /**
     * 处理触摸事件
     */
    public void onTopTouch(View view, MotionEvent event) {
        hideLevel();
        viewTemperature.setVisibility(View.VISIBLE);
        mViewCapsule.setVisibility(View.GONE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchX = (int) event.getRawX();

            // 云台双击事件
            long currentTime = new Date().getTime();
            if (currentTime - touchTime < 300) {
                isDoubleClick = true;
                touchTime = 0;
                positionx = event.getX();
                positiony = event.getY();
                if (listener != null) {
                    listener.onDoubleClick(this, (int) positionx, (int) positiony);
                }
            } else {
                isDoubleClick = false;
                touchTime = currentTime;
            }

            if (isLandscape) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isDoubleClick) {
                            changeLanscapeControlHideState();
                        }
                    }
                }, 310);
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!isLandscape) {
                int dx = (int) event.getRawX() - touchX;

                offsetx = offsetx + dx;
                if (offsetx > marginLeft) {
                    offsetx = marginLeft;
                } else if (offsetx < -marginLeft) {
                    offsetx = -marginLeft;
                }
                viewVideo.setTranslationX(offsetx);
                if (indicatorWidth != 0) {
                    circleOffsetx = -offsetx * indicatorWidth / (marginLeft * 2);
                    imageViewIndicatorCircle.setTranslationX(circleOffsetx);
                }
                touchX = (int) event.getRawX();
            }
        }
    }

    /**
     * 自定义toast和相关参数
     */
    private int toastNum;

    public void toast(String content) {
        toastNum++;
        textViewToast.setText(content);
        textViewToast.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toastNum--;
                if (toastNum == 0) {
                    textViewToast.setVisibility(View.GONE);
                    viewSpeed.setVisibility(VISIBLE);
                }
            }
        }, 3000);
    }

    /**
     * 控制器事件回调接口
     */
    public interface OnControlListener {

        // 声音开关
        void onSoundClick(MainLiveControllerView view);

        // 退出全屏
        void onOutFullScreenClick(MainLiveControllerView view);

        // 点击截图
        void onPrintScreenClick(MainLiveControllerView view);

        // 点击清晰度，level：0=流畅，1=高清，2=超清
        void onLevelClick(MainLiveControllerView view, int level, boolean isDoNewWork);

        // 点击看录像按钮
        void onRecordClick(MainLiveControllerView view);

        // 点击截图图片
        void onViewCutClick(MainLiveControllerView view);

        // 完成截图时调用
        void onPrintScreenComplete(MainLiveControllerView view, Bitmap bitmap, long imageTime);

        // 保存截图完成时调用
        void onSaveScreenImageComplete(MainLiveControllerView view);

        // 画面双击事件
        void onDoubleClick(MainLiveControllerView view, int x, int y);

        // 我的温湿度界面
        void onActionCapsuleFragment(int tem, int hum);

        // 点击自动旋转
        void onButtonAutoClick();

        // 点击云台按钮
        void onButtonCloudClick();
    }
}
