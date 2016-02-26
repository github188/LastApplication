package com.iermu.ui.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cms.media.widget.VideoView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

/**
 * Created by zhangxq on 15/7/21.
 */
public class LiveControllerView extends RelativeLayout implements View.OnClickListener {
    private Context context;
    @ViewInject(R.id.textViewControllerTitle)
    TextView textViewControllerTitle;
    @ViewInject(R.id.viewPortraitControl)
    View viewPortraitControl;
    @ViewInject(R.id.buttonFullScreen)
    ImageButton buttonFullScreen;
    @ViewInject(R.id.buttonBack)
    ImageButton buttonBack;
    @ViewInject(R.id.viewMove)
    View viewMove;
    @ViewInject(R.id.textViewZanNum)
    TextView textViewFavourNum;
    @ViewInject(R.id.viewLanscapeControl)
    View viewLanscapeControl;
    @ViewInject(R.id.buttonOutFullScreen)
    ImageButton buttonOutFullScreen;
    @ViewInject(R.id.buttonVoice)
    ImageButton buttonVoice;
    @ViewInject(R.id.buttonDanMu)
    ImageButton buttonDanMu;
    @ViewInject(R.id.viewStatus)
    PublicCamStatusView viewStatus;
    @ViewInject(R.id.viewSpeed)
    RelativeLayout viewSpeed;
    @ViewInject(R.id.speed_text)
    TextView speedText;

    private View bottomView;
    private View viewTop;
    private View viewVideo;

    // 记录状态位
    private boolean isLandscape = false;
    private boolean isShowLandscapeController = false;
    private boolean isShowProtraitController = false;
    private boolean isVoiceOn = true;
    private boolean isDanMuOn = true;

    private OnControlListener listener;

    // 页面相关参数
    private int height = 240;

    // 画面移动有关参数
    private int marginLeft;
    private int touchX;
    private int offsetx;

    public PublicCamStatusView getViewStatus() {
        return viewStatus;
    }

    public void setViewVideo(VideoView viewVideo) {
        this.viewVideo = viewVideo;
        PublicCamStatusView view = getViewStatus();
    }

    /**
     * 设置广角移动区域大小
     *
     * @param marginLeft
     */
    public void setVideoMargin(int marginLeft) {
        this.marginLeft = marginLeft;
        RelativeLayout.LayoutParams paramsVideoView = (RelativeLayout.LayoutParams) viewVideo.getLayoutParams();
        paramsVideoView.setMargins(-marginLeft, 0, -marginLeft, 0);
    }

    public void setBottomView(View bottomView) {
        this.bottomView = bottomView;
    }

    public void setViewTop(View viewTop) {
        this.viewTop = viewTop;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    public void setOnControlListener(OnControlListener listener) {
        this.listener = listener;
    }

    public void setFavourNum(int num) {
        String format = String.format(getResources().getString(R.string.zero_zan), num);
        textViewFavourNum.setText(format);
    }

    public LiveControllerView(Context context) {
        super(context);
    }

    public LiveControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_live_controller, this);
        ViewHelper.inject(this, view);

        buttonFullScreen.setOnClickListener(this);
        buttonOutFullScreen.setOnClickListener(this);
        buttonVoice.setOnClickListener(this);
        buttonDanMu.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        textViewFavourNum.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFullScreen:
                if (listener != null) {
                    listener.onFullScreenClick(this);
                }
                changeToFullScreen();
                break;
            case R.id.buttonOutFullScreen:
                if (listener != null) {
                    listener.onOutFullScreenClick(this);
                }
                changeOutOfFullScreen();
                break;
            case R.id.buttonVoice:
                if (listener != null) {
                    listener.onSoundClick(this);
                }
                changeVoiceState();
                break;
            case R.id.buttonDanMu:
                if (listener != null) {
                    listener.onDanmuClick(this);
                }
                changeDanmuState();
                break;
            case R.id.buttonBack:
                if (listener != null) {
                    listener.onBackClick(this);
                }
                break;
            case R.id.textViewZanNum:
                if (listener != null) {
                    listener.onFavourClick(this);
                }
                viewMove.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeOutUp).duration(500).playOn(viewMove);
                break;
            default:
                break;
        }
    }

    /**
     * 切换全屏
     */
    public void changeToFullScreen() {
        // 设置播放器的布局为全屏
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewTop.setLayoutParams(layoutParams);
        bottomView.setVisibility(View.GONE);

        viewVideo.setTranslationX(0);
        RelativeLayout.LayoutParams paramsVideoView = (RelativeLayout.LayoutParams) viewVideo.getLayoutParams();
        paramsVideoView.setMargins(0, 0, 0, 0);

        viewPortraitControl.setVisibility(View.GONE);
        viewLanscapeControl.setVisibility(View.GONE);

        isLandscape = true;
        isShowLandscapeController = false;
        isShowProtraitController = false;
    }

    /**
     * 切换竖屏
     */
    public void changeOutOfFullScreen() {
        // 设置播放器的布局，退出全屏
        RelativeLayout.LayoutParams layoutParams1 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewTop.setLayoutParams(layoutParams1);
        bottomView.setVisibility(View.VISIBLE);

        viewVideo.setTranslationX(offsetx);
        RelativeLayout.LayoutParams paramsVideoView1 = (RelativeLayout.LayoutParams) viewVideo.getLayoutParams();
        paramsVideoView1.setMargins(-marginLeft, 0, -marginLeft, 0);

        viewPortraitControl.setVisibility(View.GONE);
        viewLanscapeControl.setVisibility(View.GONE);

        isLandscape = false;
        isShowLandscapeController = false;
        isShowProtraitController = false;
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

    public boolean isDanMuOn() {
        return isDanMuOn;
    }

    /**
     * 设置点赞按钮是否可用
     *
     * @param enable
     */
    public void setZanEnable(boolean enable) {
        textViewFavourNum.setEnabled(enable);
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
     * 点赞界面4秒后消失
     */
    public void hideView() {
        if (isLandscape) {
            return;
        } else {
            viewPortraitControl.setVisibility(VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPortraitControl.setVisibility(INVISIBLE);
                }
            }, 3000);
        }
    }

    /**
     * 修改弹幕状态
     */
    public void changeDanmuState() {
        if (isDanMuOn) {
            buttonDanMu.setSelected(true);
        } else {
            buttonDanMu.setSelected(false);
        }
        isDanMuOn = !isDanMuOn;
    }

    /**
     * 设置控制器隐藏状态
     */
    public void changeControlHideState() {
        if (isLandscape) {
            if (isShowLandscapeController) {
                viewLanscapeControl.setVisibility(View.GONE);
            } else {
                viewLanscapeControl.setVisibility(View.VISIBLE);
            }
            isShowLandscapeController = !isShowLandscapeController;
        } else {
            if (isShowProtraitController) {
                viewPortraitControl.setVisibility(View.GONE);
            } else {
                viewPortraitControl.setVisibility(View.VISIBLE);
            }
            isShowProtraitController = !isShowProtraitController;
        }
    }

    /**
     * 视频区域触摸
     *
     * @param event
     */
    public void onTopTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchX = (int) event.getRawX();
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
                touchX = (int) event.getRawX();
            }
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
     * 获取网速
     */
    public void getSpeed(String txt) {
        if (speedText != null) speedText.setText(txt);
    }

    /**
     * 控制器事件回调接口
     */
    public interface OnControlListener {
        // 全屏
        void onFullScreenClick(LiveControllerView view);

        // 退出全屏
        void onOutFullScreenClick(LiveControllerView view);

        // 弹幕开关
        void onDanmuClick(LiveControllerView view);

        // 声音开关
        void onSoundClick(LiveControllerView view);

        // 返回
        void onBackClick(LiveControllerView view);

        // 点赞
        void onFavourClick(LiveControllerView view);
    }
}
