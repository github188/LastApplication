package com.iermu.ui.fragment.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.config.PathConfig;
import com.iermu.client.util.FileUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.util.BitmapUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.PublicCamStatusView;
import com.iermu.ui.view.TouchEventImageButton;
import com.lingyang.sdk.api.IPlayer;
import com.lingyang.sdk.api.VideoPlayerView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.Date;

/**
 * Created by zhangxq on 15/7/21.
 */
public class LyyLiveControllerView extends RelativeLayout implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
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
    @ViewInject(R.id.viewStatus)
    PublicCamStatusView viewStatus;
    @ViewInject(R.id.imageButtonSpeakCtrl)
    TouchEventImageButton imageButtonSpeak;
    @ViewInject(R.id.imageButtonPrintScreenCtrl)
    ImageButton imageButtonPrintScreen;
    @ViewInject(R.id.radioButtonLevel1)
    RadioButton mRadioButtonLevel1;
    @ViewInject(R.id.imageButtonRecordCtrl)
    ImageButton imageButtonRecord;
    @ViewInject(R.id.viewCut)
    View viewCut;
    @ViewInject(R.id.imageViewCut)
    ImageView imageViewCut;

    private boolean isLandscape = false;
    private boolean isShowController = false;
    private boolean isVoiceOn = true;
    private boolean isLevelShow = false;
    private boolean isShowLevelMessage = false;

    private VideoPlayerView viewVideo;
    private int videoMargin = 0;
    private View viewTop; // 视频区域view，包含控制器
    private int height; // 计算出来的控制器高度
    private int cutNum = 0;

    private OnControlListener listener;
    private TouchEventImageButton.TouchEventListener touchListener;

    public void setViewTop(View viewTop) {
        this.viewTop = viewTop;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setbitlevel(int level) {
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
            default:
                break;
        }
    }

    public void setVideoMargin(int videoMargin) {
        this.videoMargin = videoMargin;
    }

    public void setViewVideo(VideoPlayerView viewVideo) {
        this.viewVideo = viewVideo;
    }

    public void setSpeakTouchListener(OnTouchListener listener) {
        imageButtonSpeak.setOnTouchListener(listener);
    }


    public void setOnControlListener(OnControlListener listener) {
        this.listener = listener;
    }

    public LyyLiveControllerView(Context context) {
        super(context);
    }

    public LyyLiveControllerView(Context context, AttributeSet attrs) {
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
    }

    public PublicCamStatusView getViewStatus() {
        return viewStatus;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCloudCtrl:
                if (listener != null) {
                    listener.onCloudClick(this);
                }
                break;
            case R.id.buttonLevelCtrl:
                if (isLevelShow) {
                    radioGroup.setVisibility(View.GONE);
                } else {
                    radioGroup.setVisibility(View.VISIBLE);
                }
                isLevelShow = !isLevelShow;
                break;
            case R.id.buttonVoice:
                if (listener != null) {
                    listener.onSoundClick(this);
                }
                changeVoiceState();
                break;
            case R.id.buttonOutFullScreen:
                if (listener != null) {
                    listener.onOutFullScreenClick(this);
                }

                isLandscape = false;
                isShowController = false;
                break;
//            case R.id.viewControllerBg:
//                if (isLandscape) {
//                    if (isShowController) {
//                        // 如果显示了分辨率选择列表，则先隐藏列表
//                        if (isLevelShow) {
//                            radioGroup.setVisibility(View.GONE);
//                            isLevelShow = false;
//                            return;
//                        }
//                        viewLanscapeControl.setVisibility(View.GONE);
//                    } else {
//                        viewLanscapeControl.setVisibility(View.VISIBLE);
//                    }
//                    isShowController = !isShowController;
//                } else {
//                    if (isLevelShow) {
//                        radioGroup.setVisibility(View.GONE);
//                        isLevelShow = false;
//                    }
//                }
//                break;
            case R.id.imageButtonRecordCtrl:
                if (listener != null) {
                    listener.onRecordClick(this);
                }
                break;
            case R.id.viewCut:
                if (listener != null) {
                    listener.onViewCutClick(this);
                }
                break;
            case R.id.imageButtonPrintScreenCtrl:
                if (listener != null) {
                    listener.onPrintScreenClick(this);
                }
                break;
            default:
                break;
        }
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

    public boolean isLandscape() {
        return isLandscape;
    }

    public void hideLevel() {
        radioGroup.setVisibility(View.GONE);
        isLevelShow = false;
    }

    public void changeLanscapeControlHideState() {
        if (isLandscape) {
            if (isShowController) {
                viewLanscapeControl.setVisibility(View.GONE);
            } else {
                viewLanscapeControl.setVisibility(View.VISIBLE);
            }
            isShowController = !isShowController;
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
     * 初始化清晰度
     */


    /**
     * 处理转横屏时的操作
     */
    public void changeToLandscape() {
        viewLanscapeControl.setVisibility(View.VISIBLE);
        isLandscape = true;
        isShowController = true;

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
        layoutParamsRadioGroup.setMargins(0, Util.DensityUtil.dip2px(context, 53), Util.DensityUtil.dip2px(context, 162), 0);
        layoutParamsRadioGroup.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        radioGroup.setLayoutParams(layoutParamsRadioGroup);

        LayoutParams paramsVideoView = (LayoutParams) viewVideo.getLayoutParams();
        paramsVideoView.setMargins(0, 0, 0, 0);
    }

    /**
     * 处理转竖屏时的操作
     */
    public void changeToPortrait() {
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
        layoutParamsRadioGroup.setMargins(Util.DensityUtil.dip2px(context, 120), 0, 0, Util.DensityUtil.dip2px(context, 5));
        layoutParamsRadioGroup.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        radioGroup.setLayoutParams(layoutParamsRadioGroup);

        LayoutParams paramsVideoView1 = (LayoutParams) viewVideo.getLayoutParams();
        paramsVideoView1.setMargins(-videoMargin, 0, -videoMargin, 0);
    }

    /**
     * 显示或隐藏清晰度选择
     */
    public void changeRadioGroupState() {
        isShowLevelMessage = true;
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
    public void printScreen() {
        IPlayer iplayer = viewVideo.getPlayer();
        String imageName = new Date().getTime()+"";// + ".png"
        FileUtil.createDipPath(PathConfig.CACHE_SHARE+"/");
        iplayer.snapshot(PathConfig.CACHE_SHARE, imageName, new IPlayer.OnSnapshotListener() {
            @Override
            public void onSnapshotSuccess(String saveFullPath) {
                Logger.e("截图成功  "+saveFullPath);
                if (listener != null) {
                    Bitmap bitmap = BitmapUtil.getSmallBitmap(saveFullPath);
                    listener.onPrintScreenComplete(LyyLiveControllerView.this, saveFullPath, bitmap);
                }
            }
            @Override
            public void onSnapshotFail(int i) {
                Logger.e("截图失败 "+i);
            }
        });
//        final Bitmap bitmap = viewVideo.takePicture();
//        if (bitmap == null) {
//            return;
//        }
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//
//                String imagePath = ShareUtil.saveBitmap(bitmap);
//                Bitmap smallBitMap = ShareUtil.compressImage(bitmap, 20);
//                if (listener != null) {
//                    listener.onPrintScreenComplete(LyyLiveControllerView.this, imagePath, smallBitMap);
//                }
//            }
//        });
    }

    /**
     * 显示截图
     *
     * @param bitmap
     */
    public void setImage(Bitmap bitmap) {
        cutNum++;
        Logger.d("cutNum:" + cutNum);
        viewCut.setVisibility(View.VISIBLE);
        imageViewCut.setImageBitmap(bitmap);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                cutNum--;
                if (cutNum == 0) {
                    viewCut.setVisibility(View.GONE);
                }
            }
        }, 4000);
    }

    /**
     * 设置云台按钮的禁用
     *
     * @param isEnabled
     */
    public void setButtonCloudEnabled(boolean isEnabled) {
        buttonCloudCtrl.setEnabled(isEnabled);
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
     * 设置看录像按钮的禁用
     */
    public  void setButtonRadioEnabled(boolean isEnadble){
        imageButtonRecord.setEnabled(isEnadble);
    }

    /**
     * 设置是否显示清晰度失败提示
     *
     * @param isShowLevelMessage
     */
    public void setIsShowLevelMessage(boolean isShowLevelMessage) {
        this.isShowLevelMessage = isShowLevelMessage;
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
        if (listener != null) {
            switch (checkedId) {
                case R.id.radioButtonLevel1:
                    listener.onLevelClick(this, 0, isShowLevelMessage);
                    buttonLevelCtrl.setImageResource(R.drawable.main_live_ctrl_level1);
                    break;
                case R.id.radioButtonLevel2:
                    listener.onLevelClick(this, 1, isShowLevelMessage);
                    buttonLevelCtrl.setImageResource(R.drawable.main_live_ctrl_level2);
                    break;
                case R.id.radioButtonLevel3:
                    listener.onLevelClick(this, 2, isShowLevelMessage);
                    buttonLevelCtrl.setImageResource(R.drawable.main_live_ctrl_level3);
                    break;
                default:
                    break;
            }
            radioGroup.setVisibility(View.GONE);
            isLevelShow = false;
        }
    }

    /**
     * 控制器事件回调接口
     */
    public interface OnControlListener {

        // 声音开关
        void onSoundClick(LyyLiveControllerView view);

        // 退出全屏
        void onOutFullScreenClick(LyyLiveControllerView view);

        // 点击云台
        void onCloudClick(LyyLiveControllerView view);

        // 点击截图
        void onPrintScreenClick(LyyLiveControllerView view);

        // 点击清晰度，level：0=流畅，1=高清，2=超清
        void onLevelClick(LyyLiveControllerView view, int level, boolean isFirst);

        // 点击看录像按钮
        void onRecordClick(LyyLiveControllerView view);

        // 点击截图图片
        void onViewCutClick(LyyLiveControllerView view);

        // 完成截图时调用
        void onPrintScreenComplete(LyyLiveControllerView view, String imagePath, Bitmap bitmap);
    }
}
