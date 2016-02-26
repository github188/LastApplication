package com.iermu.ui.fragment.MineIermu;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.cms.media.player.IMediaPlayer;
import com.cms.media.widget.VideoView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.ICloudPaltformBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.IStreamMediaBusiness;
import com.iermu.client.business.dao.CloudPositionWrapper;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.config.AppConfig;
import com.iermu.client.config.PreferenceConfig;
import com.iermu.client.listener.OnAddPresetListener;
import com.iermu.client.listener.OnBitLevelChangeListener;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnCapsuleListener;
import com.iermu.client.listener.OnCheckCloudPlatFormListener;
import com.iermu.client.listener.OnCheckIsRotateListener;
import com.iermu.client.listener.OnCloudMoveListener;
import com.iermu.client.listener.OnCloudMovePresetListener;
import com.iermu.client.listener.OnCloudRotateListener;
import com.iermu.client.listener.OnDropPresetListener;
import com.iermu.client.listener.OnListPresetListener;
import com.iermu.client.listener.OnLiveMediaListener;
import com.iermu.client.listener.OnPowerCamListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamCapsule;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamStatus;
import com.iermu.client.model.CloudPosition;
import com.iermu.client.model.CloudPreset;
import com.iermu.client.model.LiveMedia;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.Logger;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.CmsTalk;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.AddPrePositionView;
import com.iermu.ui.view.CloudControllerView;
import com.iermu.ui.view.MainLiveControllerView;
import com.iermu.ui.view.PublicCamStatusView;
import com.iermu.ui.view.TouchEventImageButton;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhoushaopei on 15/6/25.
 */


public class MineLiveFragment extends BaseFragment implements View.OnClickListener, MainLiveControllerView.OnControlListener
        , OnLiveMediaListener, IMediaPlayer.OnVideoSizeChangedListener
        , IMediaPlayer.OnErrorListener, PublicCamStatusView.PublicStatusListener
        , IMediaPlayer.OnCompletionListener, IMediaPlayer.OnBufferingUpdateListener
        , IMediaPlayer.OnInfoListener, IMediaPlayer.OnPreparedListener
        , View.OnTouchListener, CmsTalk.CallBack, OnBitLevelChangeListener
        , CloudControllerView.CloudCallBack, View.OnLongClickListener
        , AddPrePositionView.AddPositionCallBack, OnCheckCloudPlatFormListener
        , OnCheckIsRotateListener, OnAddPresetListener, OnCloudMovePresetListener
        , OnDropPresetListener, OnListPresetListener, OnCloudMoveListener
        , OnCloudRotateListener, OnPowerCamListener, OnCapsuleListener, OnCamSettingListener {

    private static final String KEY_DEVICEID = "deviceId";
    private static final String KEY_IS_LANSCAPE = "isLanscape";

    @ViewInject(R.id.viewTop)
    RelativeLayout viewTop;
    @ViewInject(R.id.videoView)
    VideoView videoView;
    @ViewInject(R.id.viewController)
    MainLiveControllerView viewController;
    @ViewInject(R.id.buttonLevel)
    ImageButton buttonLevel;
    @ViewInject(R.id.buttonSound)
    ImageButton buttonSound;
    @ViewInject(R.id.imageButtonSpeak)
    ImageButton imageButtonSpeak;
    @ViewInject(R.id.imageButtonPrintScreen)
    ImageButton imageButtonPrintScreen;
    @ViewInject(R.id.buttonCloud)
    ImageButton buttonCloud;
    @ViewInject(R.id.imageButtonRecord)
    ImageButton imageButtonRecord;
    @ViewInject(R.id.buttonFullScreen)
    ImageButton buttonFullScreen;
    @ViewInject(R.id.imageViewVoice)
    ImageView imageViewVoice;

    // 云台相关控件
    @ViewInject(R.id.cloudControllerView)
    CloudControllerView cloudControllerView;
    @ViewInject(R.id.viewAddPrePositionBg)
    View viewAddPrePositionBg;
    @ViewInject(R.id.addPrePositionView)
    AddPrePositionView addPrePositionView;
    @ViewInject(R.id.viewPopPositionEdit)
    View viewPopPositionEdit;
    @ViewInject(R.id.buttonPopTitle)
    TextView buttonPopTitle;
    @ViewInject(R.id.imageViewCloudClose)
    ImageView imageViewCloudClose;
    @ViewInject(R.id.imageButtonSpeakCtrl)
    TouchEventImageButton imageButtonSpeakCtrl;
    private PublicCamStatusView viewStatus;
    private String deviceId;
    private CamLive camlive;
    private String uid;
    private ICamSettingBusiness business1;

    // 播放器状态切换相关参数
    private int userRetryNum; // 用户手动刷新次数
    private int autoRetryNum; // 后台自动重新加载次数

    // 语音
    CmsTalk talk;

    // 云台相关参数
    private boolean isCloud = false; // 记录该设备是否有云台
    private boolean isLongTouch; // 记录是否是长按事件
    private int touchx = 0; // 记录长按初始位置
    private boolean isPlaying = false; // 记录是否处于播放状态
    private List<CloudPosition> positions; // 记录预置点列表
    private boolean isAddWindowOpen; // 记录是否打开了添加弹出框
    private CloudPosition positionEditing; // 记录正在编辑的位置
    private long positionImageName; // 记录新添加预置位图片名称
    private ICloudPaltformBusiness cloudBusiness; // 云台业务层
    private int presetPosition = -1; // 记录选中的预置位，用来在自动旋转后转到预置位，-1表示不用转到预置位
    private int checkCloudNum = 0; // 记录检查是否有云台次数，最多5次

    //    码率相关参数
//    Handler mHandler;
//    NetSpeed speed;
    private Timer speedTimer;
    private List<Integer> speedList = new ArrayList<Integer>(); // 用来记录最近10次网速，计算网速平局值
    private boolean isBeginSpeed; // 记录是否开流成功，用来记录码流

    // 设置cover图
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //拷贝Bitmap对象, 因为播放器层可能有释放bitmapCover对象的代码
            bitmapCover = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            videoView.setVideoCover(bitmapCover);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    private Bitmap bitmapCover;

    // 其他参数
    int locaitonX;
    int locationY;
    int locationBottom;
    int locationRight;
    private long imageTime;

    private IPreferenceBusiness preBusiness;
    int leve;

    Timer capsuleTimer;

    // 切换码流提示参数
    private boolean isSwitchShown; // 记录是否显示过切换码流提示


    /**
     * 横屏启动个人摄像头直播页面
     *
     * @param deviceId
     * @param isLanscape
     * @return
     */
    public static Fragment actionInstance(String deviceId, boolean isLanscape) {
        MineLiveFragment fragment = new MineLiveFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEVICEID, deviceId);
        bundle.putBoolean(KEY_IS_LANSCAPE, isLanscape);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 启动个人摄像头直播页面
     *
     * @param deviceId
     * @return
     */
    public static Fragment actionInstance(String deviceId) {
        return actionInstance(deviceId, false);
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(camlive == null ? "" : camlive.getDescription());
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_lyy_mine_live, null);
        ViewHelper.inject(this, view);

        // 初始化全局参数
        Bundle bundle = getArguments();
        deviceId = bundle.getString(KEY_DEVICEID);
        Boolean isLanscape = bundle.getBoolean(KEY_IS_LANSCAPE);
        camlive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthT = displayMetrics.widthPixels;
        int heightT = displayMetrics.heightPixels;


        // 设置cover图
        if (camlive.getThumbnail() != null && camlive.getThumbnail().length() > 0) {
            Picasso.with(getActivity()).load(camlive.getThumbnail()).into(target);
        }

        // 设置屏幕相关参数
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (isLanscape) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // 标题初始化
        setCommonTitle(camlive.getDescription());
        viewController.setTitle(camlive.getDescription());

//        //获取view的坐标
        int[] location = new int[2];
        imageButtonSpeakCtrl.getLocationInWindow(location);
        int heiht = imageButtonSpeakCtrl.getHeight();
        int width = imageButtonSpeakCtrl.getWidth();
        int top = imageButtonSpeakCtrl.getTop();
        int left = imageButtonSpeakCtrl.getLeft();
        int right = imageButtonSpeakCtrl.getRight();
        int bottom = imageButtonSpeakCtrl.getBottom();
        locaitonX = left;
        locationY = top;
        locationBottom = bottom;
        locationRight = right;

        // 初始化触摸事件
        imageButtonSpeak.setOnTouchListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnVideoSizeChangedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnBufferingUpdateListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnPreparedListener(this);
        videoView.setOnBufferingUpdateListener(this);
        setEnabled(false);
        buttonCloud.setEnabled(false);
        viewController.setButtonCloudEnabled(false);
        viewTop.setOnTouchListener(this);
        viewTop.setOnLongClickListener(this);
        viewController.setOnControlListener(this);
        viewController.setViewTop(viewTop);
        viewController.setViewVideo(videoView);
        viewController.setSpeakTouchListener(this);
        cloudControllerView.setListener(this);
        addPrePositionView.setListener(this);
        addPrePositionView.setOnClickListener(this);

        // 设置视频播放高度和移动空间
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels;
        int screenHeight = metric.heightPixels;
        int videoHeight = 0;
        if (screenHeight > screenWidth) {
            videoHeight = screenWidth * 3 / 4;
        } else {
            videoHeight = screenHeight * 3 / 4;
            hideActionBar();
        }
        viewController.setVideoHeight(videoHeight);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewTop.getLayoutParams();
        params.height = videoHeight;
        final int videoWidth = videoHeight * 16 / 9;
        if (screenHeight > screenWidth) {
            viewController.setLeftAndRightMergin((videoWidth - screenWidth) / 2);
        } else {
            viewController.setLeftAndRightMergin((videoWidth - screenHeight) / 2);
        }

        // 声音开关初始化
        preBusiness = ErmuBusiness.getPreferenceBusiness();
        boolean isSoundOpen = preBusiness.getSoundStatus(deviceId, PreferenceConfig.SoundType.mineCam);
        if (isSoundOpen != viewController.isVoiceOn()) {
            viewController.changeVoiceState();
            onSoundStateChange();
        }

        // 初始化设置信息
        business1 = ErmuBusiness.getCamSettingBusiness();
        CamStatus status = business1.getCamStatus(deviceId);
        Logger.d("camstatuslevel:" + status);
        if (status == null) {
            business1.syncCamStatus(deviceId);
            business1.registerListener(OnCamSettingListener.class, this);
        }
        leve = (status != null) ? status.getBitlevel() : 0;
        viewController.setbitlevel(leve, false);
        business1.registerListener(OnBitLevelChangeListener.class, this);
        ErmuBusiness.getCamSettingBusiness().registerListener(OnPowerCamListener.class, this);

        // 初始化空气胶囊
        capsuleTimer = new Timer();
        capsuleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                business1.getCapsule(deviceId);
            }
        }, 10, 600 * 1000);
        business1.registerListener(OnCapsuleListener.class, this);

        // 对讲初始化
        talk = new CmsTalk();
        initTalk();
        // 云录制状态初始化
        long curDate = new Date().getTime();
        long endDate = camlive.getCvrEndTime();
        long offect = curDate - endDate;
        if (Integer.parseInt(camlive.getCvrDay()) > 0 || offect / 86400000 < 0) {
            setButtonRecordEnabled(true);
        } else {
            if (camlive.getConnectType() == ConnectType.LINYANG) {
                setButtonRecordEnabled(true);
            } else {
                setButtonRecordEnabled(false);
            }
        }

        // 初始化云台
        cloudBusiness = ErmuBusiness.getCloudPlatFormBusiness();
        cloudBusiness.checkCloudPlatForm(deviceId);
        cloudBusiness.checkIsRotate(deviceId);
        cloudBusiness.registerListener(OnCheckCloudPlatFormListener.class, this);
        cloudBusiness.registerListener(OnCheckIsRotateListener.class, this);
        cloudBusiness.registerListener(OnCloudMoveListener.class, this);
        cloudBusiness.registerListener(OnCloudMovePresetListener.class, this);
        cloudBusiness.registerListener(OnCloudRotateListener.class, this);
        positions = CloudPositionWrapper.getCloudPositions(uid, deviceId);
        cloudControllerView.updatePositions(positions);

        speedTxt();//显示网速
//        // 初始化码率
//        try {
//            mHandler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    if (msg.what == 1) {
//                        viewController.getSpeed(msg.arg1 + "KB");
//                    }
//                    super.handleMessage(msg);
//                }
//            };
//            speed = NetSpeed.getInstant(getActivity(), mHandler, videoView);
//            speed.startCalculateNetSpeed();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // 播放录像
        IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
        business.registerListener(OnLiveMediaListener.class, this);

        // 播放状态初始化
        viewStatus = viewController.getViewStatus();
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camlive.getThumbnail(), getString(R.string.in_loading_please), null);
        viewStatus.setListener(this);
        if (refreshStatusView()) {
            business.openLiveMedia(deviceId);
        }
//        timer = new Timer();
//        task = new CustomTask();
//        timer.schedule(task, 0, 1500);
        return view;
    }

    private long m_iStartTime = 0;
    private long m_iOpenTime = 0;

    private void speedTxt() {
        videoView.post(new Runnable() {
            @Override
            public void run() {
                viewController.getSpeed((int) videoView.getNetbps() + "");
            }
        });
        speedTimer = new Timer();
        speedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                final int net = (int) videoView.getNetbps();
//                Logger.i("Netbps:" + net);
                videoView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (viewController != null) {
                            viewController.getSpeed(net + "");
//                            int fps = (int) videoView.getFps();
//                            float fpsf = videoView.getFps();
//                            Logger.d("fps:" + fpsf + "___int:" + fps);
//                            if (isBeginSpeed) {
//                                if (fps > 0) {
//                                    if (speedList.size() < 10) {
//                                        speedList.add(fps);
//                                    } else {
//                                        speedList.add(fps);
//                                        speedList.remove(0);
//                                        int speedSum = 0;
//                                        for (Integer speed : speedList) {
//                                            speedSum += speed;
//                                        }
//                                        int speedAvg = speedSum / speedList.size();
//                                        if (speedAvg < 8 && !isSwitchShown && leve > 0 && viewStatus != null) {
//                                            viewStatus.showViewSwitch();
//                                            isSwitchShown = true;
//                                            viewController.speedMove(true);
//                                        }
//                                    }
//                                }
//                            }
                        }
                    }
                });
            }
        }, 0, 500);
    }

//    Timer timer;
//    TimerTask task;

//    class CustomTask extends TimerTask {
//        @Override
//        public void run() {
//            long bps = videoView.getNetbps();
//            float fps = videoView.getFps();
//            float dps = videoView.getDps();
//            String strPlayInfo = videoView.getPlayInfo();
//            if (strPlayInfo == null) strPlayInfo = "";
//            else strPlayInfo = "\n" + strPlayInfo;
//            final String strTip = String.format("phn: net=%4dKB", bps).toString()
//                    + ", " + String.format("fps=%2.0f", fps).toString()
//                    + ", " + String.format("dps=%2.0f", dps).toString()
//                    + ", " + String.format("open=%4d", m_iOpenTime).toString()
//                    + strPlayInfo;
//            //Logger.i(strTip);
//            videoView.post(new Runnable() {
//                @Override
//                public void run() {
//                    viewController.getSpeed(strTip);
//                }
//            });
//        }
//    }

    /**
     * ********************************系统事件回调****************************************************
     */

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewController.changeToPortrait();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            showActionBar();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (viewAddPrePositionBg.getVisibility() == View.VISIBLE) {
                if (isAddWindowOpen) {
                    animateHideAddView();
                } else {
                    animateHidePopView(false);
                }
            }
            viewController.changeToLandscape();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideActionBar();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        imageViewVoice.setVisibility(View.INVISIBLE);
        viewController.setAudioStrengthImageShow(false);
        buttonFullScreen.setEnabled(true);
        viewController.setButtonOutFullScreenEnable(true);
        if (bitmapCover != null && !bitmapCover.isRecycled()) {
            videoView.setVideoCover(bitmapCover);
        }
        Logger.i("onHiddenChanged hidden:" + hidden);
        if (hidden) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    videoView.setVisibility(View.INVISIBLE);
                }
            }, 200);
        } else {
            videoView.setVisibility(View.VISIBLE);
            setEnabled(false);
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camlive.getThumbnail(), getString(R.string.in_loading_please), null);
            IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
            business.openLiveMedia(deviceId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!existBackStackTop()) {
            return;
        }
        Logger.i("onResume");
        addPrePositionView.hideKeyboard();
        if (!viewStatus.isViewStatusShow()) {  //第二次加载页面
            Logger.d("onResume reload");
            setEnabled(false);
            if (!Util.isNetworkAvailable(getActivity())) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.play_no_network), null);
            } else {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camlive.getThumbnail(), getString(R.string.in_loading_please), null);
                IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
                business.openLiveMedia(deviceId);
                if (videoView != null) videoView.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!existBackStackTop()) {
            return;
        }
        if (speedTimer != null) speedTimer.cancel();
//        if (task != null) task.cancel();
//        if (timer != null) timer.cancel();
        ErmuBusiness.getStreamMediaBusiness().unRegisterListener(OnLiveMediaListener.class, this);

        addPrePositionView.hideKeyboard();
        Logger.i("onPause");
        Logger.i("MimeLiveFragment", "onPause closeLiveMedia start");
        ErmuBusiness.getStreamMediaBusiness().closeLiveMedia(deviceId);
        Logger.i("MimeLiveFragment", "onPause closeLiveMedia end");
        if (videoView != null) videoView.stopPlayback(false);
        Logger.i("MimeLiveFragment", "onPause stopPlayback end");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (speedTimer != null) speedTimer.cancel();
//        if (task != null) task.cancel();
//        if (timer != null) timer.cancel();
        if (capsuleTimer != null) capsuleTimer.cancel();
        viewController.timerCancel();
//        if (speed != null) speed.stopCalculateNetSpeed();
        Picasso.with(getActivity()).cancelRequest(target);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
        business.unRegisterListener(OnLiveMediaListener.class, this);

        business.closeLiveMedia(deviceId);

        business1.unRegisterListener(OnBitLevelChangeListener.class, this);
        business1.unRegisterListener(OnCamSettingListener.class, this);
        cloudBusiness.unRegisterListener(OnCheckCloudPlatFormListener.class, this);
        cloudBusiness.unRegisterListener(OnCheckIsRotateListener.class, this);
        cloudBusiness.unRegisterListener(OnCloudMovePresetListener.class, this);
        cloudBusiness.unRegisterListener(OnCloudMoveListener.class, this);
        cloudBusiness.unRegisterListener(OnCloudRotateListener.class, this);
        cloudBusiness.unRegisterListener(OnCapsuleListener.class, this);
        ErmuBusiness.getCamSettingBusiness().unRegisterListener(OnPowerCamListener.class, this);

        Logger.i("MimeLiveFragment", "onDestroyView stopPlayback start");
        if (videoView != null) videoView.stopPlayback(true);
        Logger.i("MimeLiveFragment", "onDestroyView stopPlayback end");

        // 清除屏幕不锁定参数
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 释放语音对讲资源
        talk.releaseAudioRecorder();

        // 隐藏键盘
        addPrePositionView.hideKeyboard();
    }

    @OnClick(value = {R.id.buttonCloud, R.id.buttonLevel, R.id.buttonSound, R.id.buttonFullScreen
            , R.id.imageButtonRecord, R.id.imageButtonSpeak, R.id.imageButtonPrintScreen
            , R.id.viewAddPrePositionBg, R.id.buttonPopCancel, R.id.buttonPopDelete, R.id.buttonPopEdit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCloud:
                if (cloudControllerView.isShowPosition()) {
                    cloudControllerView.hideGridViewPosition();
                    buttonCloud.setImageResource(R.drawable.cloud_close_up);
                } else {
                    if (viewController.isInCloud()) {
                        cloudControllerView.setVisibility(View.GONE);
                        imageViewCloudClose.setVisibility(View.GONE);
                        if (cloudControllerView.isAutoStart()) {
                            buttonCloud.setImageResource(R.drawable.cloud_button_auto);
                        } else {
                            buttonCloud.setImageResource(R.drawable.main_live_cloud);
                        }
                    } else {
                        cloudControllerView.setVisibility(View.VISIBLE);
                        imageViewCloudClose.setVisibility(View.VISIBLE);
                        buttonCloud.setImageResource(R.drawable.cloud_close_up);
                    }
                    viewController.setViewCloudTipVisible(viewController.isInCloud());
                    viewController.setIsInCloud(!viewController.isInCloud());
                }
                break;
            case R.id.buttonLevel:
                viewController.changeRadioGroupState();
                break;
            case R.id.buttonSound:
                viewController.hideLevel();
                viewController.changeVoiceState();
                onSoundStateChange();
                break;
            case R.id.buttonFullScreen:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case R.id.imageButtonRecord:
                imageButtonRecord.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageButtonRecord.setEnabled(true);
                    }
                }, 100);
                Fragment fragment = MainRecordFragment.actionInstance(deviceId);
                addToBackStack(fragment, true);
                break;
            case R.id.imageButtonSpeak:
                break;
            case R.id.imageButtonPrintScreen:
                viewController.displaySpeed();
                viewController.hideLevel();
                viewController.printScreen(true);
                printScreenTime(100);
                viewController.hideSpeed();
                break;
            case R.id.viewAddPrePositionBg:
                if (isAddWindowOpen) {
                    animateHideAddView();
                } else {
                    animateHidePopView(false);
                }
                break;
            case R.id.buttonPopEdit:
                animateHidePopView(true);
                break;
            case R.id.buttonPopDelete:
                if (positionEditing != null) {
//                    cloudBusiness.dropPreset(deviceId, positionEditing.getPreset());
                    CloudPositionWrapper.deletePosition(positionEditing.getUid(), positionEditing.getDeviceId(), positionEditing.getPreset());
                    positions = CloudPositionWrapper.getCloudPositions(uid, deviceId);
                    cloudControllerView.updatePositions(positions);
                }
                animateHidePopView(false);
                break;
            case R.id.buttonPopCancel:
                animateHidePopView(false);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        viewController.hideLevel();
        if (v.getId() == R.id.viewTop) {
            viewController.onTopTouch(v, event);
            // 设置是否无视长按事件
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isLongTouch = true;
                touchx = (int) event.getRawX();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                int x = (int) event.getRawX();
                if (Math.abs(touchx - x) > 25) {
                    isLongTouch = false;
                }
            }
        } else if (v.getId() == R.id.imageButtonSpeak || v.getId() == R.id.imageButtonSpeakCtrl) {
            if (Util.isNetworkConn(getActivity())) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imageViewVoice.setVisibility(View.VISIBLE);
                    viewController.setAudioStrengthImageShow(true);
                    buttonFullScreen.setEnabled(false);
                    viewController.setButtonOutFullScreenEnable(false);
                    // 禁用全屏切换
                    if (viewController.isLandscape()) {
                        // 禁用全屏状态下的按钮自动隐藏
                        viewController.setIsSpeaking(true);
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    } else {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    }

                    videoView.mute(1);
                    if (camlive.getConnectType() == ConnectType.LINYANG) {
                        talk.startAudio();
                    } else {
                        String token = ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken();
                        talk.connectAudio(deviceId, token, camlive.getStreamId());
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewController.setVoiceOn(true);
                    buttonSound.setSelected(false);
                    videoView.mute(0);// 声音开
                    imageViewVoice.setVisibility(View.INVISIBLE);
                    viewController.setAudioStrengthImageShow(false);
                    buttonFullScreen.setEnabled(true);
                    viewController.setButtonOutFullScreenEnable(true);
                    viewController.setIsSpeaking(false);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    imageButtonSpeak.setEnabled(false);
                    viewController.setButtonSpeakEnabled(false);
                    talk.stopAudio();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = event.getX();
                    float y = event.getY();
                    int viewX = (int) x;//手指滑动的左边
                    int viewY = (int) y;
                    if ((viewX < locaitonX || viewY < locationY) && imageViewVoice.getVisibility() != View.INVISIBLE) {
                        viewController.setVoiceOn(true);
                        buttonSound.setSelected(false);
                        videoView.mute(0);// 声音开
                        imageViewVoice.setVisibility(View.INVISIBLE);
                        viewController.setAudioStrengthImageShow(false);
                        buttonFullScreen.setEnabled(true);
                        viewController.setButtonOutFullScreenEnable(true);
                        viewController.setIsSpeaking(false);
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        if (hasAudioPermission()) {
                            imageButtonSpeak.setEnabled(false);
                            viewController.setButtonSpeakEnabled(false);
                            talk.stopAudio();
                        }
                    }
//                    else if (viewX > locationRight && imageViewVoice.getVisibility() != View.INVISIBLE){
//                        videoView.mute(0);
//                        imageViewVoice.setVisibility(View.INVISIBLE);
//                        viewController.setAudioStrengthImageShow(false);
//                        viewController.setIsSpeaking(false);
//                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                        if (hasAudioPermission()) {
//                            imageButtonSpeak.setEnabled(false);
//                            viewController.setButtonSpeakEnabled(false);
//                            talk.stopAudio();
//                        }
//                    }
//                    if (viewY  < - locationBottom && imageViewVoice.getVisibility() != View.INVISIBLE){
//                        videoView.mute(0);
//                        imageViewVoice.setVisibility(View.INVISIBLE);
//                        viewController.setAudioStrengthImageShow(false);
//                        viewController.setIsSpeaking(false);
//                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                        if (hasAudioPermission()) {
//                            imageButtonSpeak.setEnabled(false);
//                            viewController.setButtonSpeakEnabled(false);
//                            talk.stopAudio();
//                        }
//                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.viewTop) {
            if (!viewController.isLandscape() && viewController.isInCloud() && isLongTouch && isPlaying && !cloudControllerView.isAutoStart()) {
                if (positions.size() >= 6) {
                    viewController.toast(getString(R.string.preset_num));
                    return false;
                }
                positionEditing = null;
                final Bitmap bitmap = videoView.takePicture();
                Bitmap bitmapSmall = ShareUtil.compressImage(bitmap, 20);
                addPrePositionView.setImage(bitmapSmall);
                animateShowAddView();
                positionImageName = new Date().getTime();
                ErmuApplication.execBackground(new Runnable() {
                    @Override
                    public void run() {
                        ShareUtil.savePresetBitmap(bitmap, positionImageName);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cloudControllerView.updatePositionsImage();
                                }
                            });
                        }
                    }
                });
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewAddPrePositionBg.getVisibility() == View.VISIBLE) {
                if (isAddWindowOpen) {
                    animateHideAddView();
                } else {
                    animateHidePopView(false);
                }

                return true;
            }

            if (viewController.isLandscape()) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ********************************播放器事件回调****************************************************
     */

    @Override
    public void onLiveMediaChanged(String devId) {
        if (!devId.equals(deviceId)) return;
        Logger.i("onLiveMediaChanged", "deviceId:" + devId);
        LiveMedia media = ErmuBusiness.getStreamMediaBusiness().getLiveMedia(deviceId);
        Logger.i("media", "isnull:" + (media == null));
        if (media == null) {
            if (!Util.isNetworkConn(getActivity())) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.play_no_network), null);
            } else {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.load_fail), null);
            }
            return;
        }

        int connectType = media.getConnectType();
        String playUrl = media.getPlayUrl();
        int status = media.getStatus();
        int connectRet = media.getConnectRet();
        //boolean isLanRtmp = media.isLanRtmp();

        ErmuBusiness.getStatisticsBusiness().statStartPlay(deviceId, connectType, connectRet, status);
        m_iStartTime = System.currentTimeMillis();

        videoView.bufferON(false);
        videoView.setDelayMS(500);
        if (connectType == ConnectType.BAIDU) {
            Logger.i("onLiveMediaChanged ", "BAIDU playUrl:" + playUrl);
            videoView.playVideo(playUrl, false);
            refreshBaiduStatusView(playUrl);
        } else if (connectType == ConnectType.LINYANG) {
            Logger.i("onLiveMediaChanged ", "LINYANG playUrl:" + playUrl);

            //摄像机离线             status
            //羚羊状态不对            p2p、state
            //羚羊打开直播不对        p2p、connect | rtmp、connect
            //羚羊状态对  打开直播对  p2p、state、connect | rtmp、connect
            //正在连接摄像机         p2p、state | rtmp
            if (media.isOffline()) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getString(R.string.dev_offline), null);
                return;
            } else if (media.isOffLive() || !media.isConnected()) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getString(R.string.conn_dev_fail), null);
                return;
            } else/* if (media.isLiveOn())*/ {
                videoView.playVideo(playUrl, false);
            }// else {
            //    viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camlive.getThumbnail(), getString(R.string.in_loading_please), null);
            //}
        }
        videoView.setDeviceID(devId); //在playvideo接口后调用这个接口，退出播放后会向后台服务器上传播放日志
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {

    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i2) {
        if (!existBackStackTop()) {
            return false;
        }
        Logger.i("onError", "errorcode:" + i);
        setEnabled(false);
        isBeginSpeed = false;
//        if(talk!=null) talk.stopAudio();
        videoView.stopPlayback(false);
        if (bitmapCover != null && !bitmapCover.isRecycled()) {
            videoView.setVideoCover(bitmapCover);
        }
        imageViewVoice.setVisibility(View.INVISIBLE);
        viewController.setAudioStrengthImageShow(false);
        buttonFullScreen.setEnabled(true);
        viewController.setButtonOutFullScreenEnable(true);
        if (i == -10000) {
            if (!ErmuApplication.isConnected()) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.play_no_network), null);
            } else if (camlive.isOffline()) {
                if (userRetryNum >= 4) {
                    userRetryNum = 0;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_RESOLVE, null, getString(R.string.refresh_over_times), null);
                } else {
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getString(R.string.dev_off_line), null);
                }
            } else {
                if (autoRetryNum >= 2) {
                    autoRetryNum = 0;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.load_fail), null);
                } else {
                    autoRetryNum++;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camlive.getThumbnail(), getString(R.string.in_loading_please), null);
                    IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
                    business.reloadLiveMedia(deviceId);
                }
            }
        }

        switch (i) {
            case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Logger.i("Play Error:::", "MEDIA_ERROR_SERVER_DIED");
                break;
            case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
                Logger.i("Play Error:::", "MEDIA_ERROR_UNKNOWN");
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        Logger.i("onCompletion " + iMediaPlayer.getCurrentPosition());
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        Logger.i("onBufferingUpdate " + i);
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i2) {
        Logger.i("onInfo i=" + i + " i2=" + i2);
        switch (i) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START://第一帧上屏显示
                setEnabled(true);
                isBeginSpeed = true;
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:   //显示进度条
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:     //隐藏进度条
                break;
            case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                break;
            case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                break;
            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        if (!viewController.isVoiceOn()) {
            videoView.mute(1);// 声音关
        }
        Logger.i("onPrepared " + iMediaPlayer.getCurrentPosition());
        viewStatus.hideViewStatus();
        userRetryNum = 0;
        autoRetryNum = 0;
        //TODO 隐藏进度条
        //当前时间为:直播播放时间
        //1.计算直播播放时间-直播开始时间=直播打开时间
        m_iOpenTime = System.currentTimeMillis() - m_iStartTime;
        Logger.i("开流时间: " + m_iOpenTime);
    }

    /**
     * ********************************控制器事件回调****************************************************
     */

    @Override
    public void onOutFullScreenClick(MainLiveControllerView view) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    @Override
    public void onPrintScreenClick(MainLiveControllerView view) {
        viewController.printScreen(true);
        printScreenTime(100);
    }

    @Override
    public void onLevelClick(MainLiveControllerView view, int level, boolean isDoNekWork) {
        if (level > 0) {
            speedList.clear();
        }
        switch (level) {
            case 0:
                buttonLevel.setImageResource(R.drawable.main_live_level1);
                if (isDoNekWork) {
                    ErmuBusiness.getCamSettingBusiness().setBitLevel(deviceId, 0);
                }
                break;
            case 1:
                buttonLevel.setImageResource(R.drawable.main_live_level2);
                if (isDoNekWork) {
                    ErmuBusiness.getCamSettingBusiness().setBitLevel(deviceId, 1);
                }
                break;
            case 2:
                buttonLevel.setImageResource(R.drawable.main_live_level3);
                if (isDoNekWork) {
                    ErmuBusiness.getCamSettingBusiness().setBitLevel(deviceId, 2);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRecordClick(MainLiveControllerView view) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Fragment fragment = MainRecordFragment.actionInstance(deviceId, true);
        addToBackStack(fragment, true);
    }

    @Override
    public void onViewCutClick(MainLiveControllerView view) {
        //TODO 解决进入截屏分享页面, 重新返回直播页面崩溃问题(解: 进入分享页面后, 需要closeConnect)
        Logger.i("MimeLiveFragment", "onViewCutClick closeLiveMedia start");
        ErmuBusiness.getStreamMediaBusiness().closeLiveMedia(deviceId);
        Logger.i("MimeLiveFragment", "onViewCutClick closeLiveMedia end");
        Logger.i("MimeLiveFragment", "onViewCutClick stopPlayback start");
        if (videoView != null) videoView.stopPlayback(false);
        Logger.i("MimeLiveFragment", "onViewCutClick stopPlayback end");
        //TODO end
        Fragment fragment = ShareImageFragment.actionInstance(camlive.getDescription(), imageTime);
        addToBackStack(fragment);
    }

    @Override
    public void onPrintScreenComplete(MainLiveControllerView view, final Bitmap bitmap, long imageTime) {
        ShareUtil.saveBitmap(bitmap, imageTime, deviceId);
        this.imageTime = imageTime;
        if (bitmap == null) {
            return;
        }
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewController != null) viewController.setImage(bitmap);
                }
            });
        }
    }

    @Override
    public void onSaveScreenImageComplete(MainLiveControllerView view) {
        Intent intent = new Intent();
        intent.setAction(ShareImageFragment.INTENT_SAVE_IMAGE_COMPLETE);
        if (getActivity() != null) {
            getActivity().sendBroadcast(intent);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewController != null) viewController.imageCutToast();
                }
            });
        }
    }

    @Override
    public void onDoubleClick(MainLiveControllerView view, int xEnd, int yEnd) {
        if (viewController.isInCloud() && isPlaying && !cloudControllerView.isAutoStart()) {
            DisplayMetrics metric = new DisplayMetrics();
            getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
            int screenWidth = metric.widthPixels;
            int xCenter = screenWidth / 2;
            int screenHeight = screenWidth * 3 / 4;
            int yCenter = screenHeight / 2;
            if (viewController.isLandscape()) {
                screenHeight = metric.heightPixels;
                yCenter = screenHeight / 3;
                xCenter = screenWidth / 3;
                xEnd = xEnd * 2 / 3;
                yEnd = yEnd * 2 / 3;
            }
            cloudBusiness.cloudMove(deviceId, xEnd, yEnd, xCenter, yCenter);
            viewController.animateCenter(screenWidth / 2, screenHeight / 2);
            viewController.showTouchPosition();
        }
    }

    @Override
    public void onActionCapsuleFragment(int tem, int hum) {
        StatService.onEvent(getActivity(), "check_capsule", "pass", 1);
        long currentTime = new Date().getTime();
        final Bitmap bitmap = videoView.takePicture();
        if (Util.isNetworkAvailable(getActivity())) {
            if (bitmap != null) {
//                ShareUtil.saveVideoBitmap(bitmap, currentTime);
                Fragment fragment = CapsuleFragment.actionInstance(getActivity(), tem, hum, currentTime, bitmap);
                addToBackStack(fragment);
            } else {
                ErmuApplication.toast(getString(R.string.screen_shot_fail));
            }
        } else {
            ErmuApplication.toast(getString(R.string.no_net));
        }
    }

    @Override
    public void onSoundClick(MainLiveControllerView view) {
        onSoundStateChange();
    }

    /**
     * ********************************状态转换器事件回调****************************************************
     */

    @Override
    public void onRefreshClick(PublicCamStatusView view) {
        userRetryNum++;
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camlive.getThumbnail(), getString(R.string.in_loading_please), null);
        ErmuBusiness.getStreamMediaBusiness().openLiveMedia(deviceId);
    }

    @Override
    public void onButtonResolveClick(PublicCamStatusView view) {
        WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_SOLVE);
    }

    @Override
    public void onButtonStartClick(PublicCamStatusView view) {
        //开机
        ErmuBusiness.getCamSettingBusiness().powerCamDev(deviceId, true);
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camlive.getThumbnail(), getString(R.string.in_loading_please), null);
    }

    @Override
    public void onSwitchClick(PublicCamStatusView view) {
        viewController.setbitlevel(0, true);
    }

    @Override
    public void onSwitchViewHide(PublicCamStatusView view) {
        viewController.speedMove(false);
    }

    /**
     * ********************************对讲事件回调****************************************************
     */

    @Override
    public void onStrengthChanged(final int strength) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (strength < 15) {
                    imageViewVoice.setImageResource(R.drawable.audio_strength_1);
                    viewController.setAudioStrengthImage(R.drawable.audio_strength_ctrl_1);
                } else if (strength >= 15 && strength < 18) {
                    imageViewVoice.setImageResource(R.drawable.audio_strength_2);
                    viewController.setAudioStrengthImage(R.drawable.audio_strength_ctrl_2);
                } else if (strength >= 18 && strength < 21) {
                    imageViewVoice.setImageResource(R.drawable.audio_strength_3);
                    viewController.setAudioStrengthImage(R.drawable.audio_strength_ctrl_3);
                } else if (strength >= 21 && strength < 24) {
                    imageViewVoice.setImageResource(R.drawable.audio_strength_4);
                    viewController.setAudioStrengthImage(R.drawable.audio_strength_ctrl_4);
                } else if (strength >= 24 && strength < 27) {
                    imageViewVoice.setImageResource(R.drawable.audio_strength_5);
                    viewController.setAudioStrengthImage(R.drawable.audio_strength_ctrl_5);
                } else if (strength >= 27 && strength < 30) {
                    imageViewVoice.setImageResource(R.drawable.audio_strength_6);
                    viewController.setAudioStrengthImage(R.drawable.audio_strength_ctrl_6);
                } else if (strength >= 30 && strength < 33) {
                    imageViewVoice.setImageResource(R.drawable.audio_strength_7);
                    viewController.setAudioStrengthImage(R.drawable.audio_strength_ctrl_7);
                } else if (strength >= 33) {
                    imageViewVoice.setImageResource(R.drawable.audio_strength_8);
                    viewController.setAudioStrengthImage(R.drawable.audio_strength_ctrl_8);
                }
            }
        });
    }

    @Override
    public void onConnectClosed() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageButtonSpeak.setEnabled(true);
                    viewController.setButtonSpeakEnabled(true);
                }
            });
        }
    }

    /**
     * ********************************云台事件回调****************************************************
     */

    @Override
    public void onStartAutoClick() {
        cloudBusiness.startCloudRotate(deviceId);
        viewController.showCloudTip();
        viewController.setCloudTipText(getString(R.string.rotate_everywhere));
        viewController.setIsStartAuto(true);
    }

    @Override
    public void onStopAutoClick() {
        cloudBusiness.stopCloudRotate(deviceId);
        viewController.showCloudTip();
        viewController.setCloudTipText(getString(R.string.double_click_rotate));
        viewController.setIsStartAuto(false);
    }

    @Override
    public void onPrePositionClick() {
        buttonCloud.setImageResource(R.drawable.cloud_position_return);
    }

    @Override
    public void onCheckCloudPlatForm(Business business, boolean b, String deviceId) {
        if (!this.deviceId.equals(deviceId)) {
            return;
        }
        Logger.d("cloudPlatForm:" + b + "_num:" + checkCloudNum);
        viewController.setCapsuleLayInvisible(b);
        if (business.isSuccess()) {
            isCloud = b;
            if (isPlaying) {
                buttonCloud.setEnabled(b);
                viewController.setButtonCloudEnabled(b);
            }
        } else {
            if (checkCloudNum < 5) {
                checkCloudNum++;
                cloudBusiness.checkCloudPlatForm(deviceId);
            }
        }
    }

    @Override
    public void onCheckIsRotate(Business business, boolean b) {
        if (business.isSuccess()) {
            Logger.d("isRotate:" + b);
            cloudControllerView.setIsStartAuto(b);
            viewController.setIsStartAuto(b);
            if (viewController.isInCloud()) {
                viewController.showCloudTip();
            }
            if (b) {
                viewController.setCloudTipText(getString(R.string.rotate_everywhere));
                buttonCloud.setImageResource(R.drawable.cloud_button_auto);
                viewController.setIsInCloud(false);
            } else {
                viewController.setCloudTipText(getString(R.string.double_click_rotate));
            }
        } else {

        }
    }

    @Override
    public void onAddPreset(Business business, int preset) {
        if (!business.isSuccess()) {

        }
    }

    @Override
    public void onDropPreset(Business business) {
        if (!business.isSuccess()) {

        }
    }

    @Override
    public void onCloudMovePreset(Business business) {
        if (!business.isSuccess()) {

        }
    }

    @Override
    public void onListPreset(Business business, List<CloudPreset> list, int count) {
        if (!business.isSuccess()) {

        }
    }

    @Override
    public void onCloudMove(Business business, int num) {
        if (business.isSuccess()) {
            if (num == 2) {
                viewController.animateMostLeft();
            } else if (num == 1) {
                viewController.animateMostRight();
            }
        } else {

        }
    }

    @Override
    public void onCloudRotate(Business business) {
        if (!business.isSuccess()) {

        }
    }

    @Override
    public void onButtonAutoClick() {
        if (viewController.isAutoStart()) {
            cloudBusiness.startCloudRotate(deviceId);
            if (viewController.isInCloud()) {
                viewController.showCloudTip();
                viewController.setCloudTipText(getString(R.string.rotate_everywhere));
            }
        } else {
            cloudBusiness.stopCloudRotate(deviceId);
            if (viewController.isInCloud()) {
                viewController.showCloudTip();
                viewController.setCloudTipText(getString(R.string.double_click_rotate));
            }
        }
        cloudControllerView.setIsStartAuto(viewController.isAutoStart());
    }

    @Override
    public void onButtonCloudClick() {
        if (viewController.isInCloud()) {
            cloudControllerView.hideGridViewPosition();
            cloudControllerView.setVisibility(View.GONE);
            imageViewCloudClose.setVisibility(View.GONE);
            if (cloudControllerView.isAutoStart()) {
                buttonCloud.setImageResource(R.drawable.cloud_button_auto);
            } else {
                buttonCloud.setImageResource(R.drawable.main_live_cloud);
            }
        } else {
            cloudControllerView.setVisibility(View.VISIBLE);
            imageViewCloudClose.setVisibility(View.VISIBLE);
            buttonCloud.setImageResource(R.drawable.cloud_close_up);
        }
        viewController.setViewCloudTipVisible(viewController.isInCloud());
    }

    /**
     * ********************************预置位编辑弹框事件回调****************************************************
     */

    @Override
    public void onPositionEditClick(int position) {
        CloudPosition cloudPosition = positions.get(position);
        positionEditing = cloudPosition;
        buttonPopTitle.setText(cloudPosition.getTitle());
        viewAddPrePositionBg.setVisibility(View.VISIBLE);
        isAddWindowOpen = false;
        YoYo.with(Techniques.SlideInUp).duration(300).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewPopPositionEdit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(viewPopPositionEdit);
    }

    @Override
    public void onPositionClick(final int position) {
        if (cloudControllerView.isAutoStart()) {
            cloudControllerView.setIsStartAuto(false);
            viewController.setIsStartAuto(false);
            onStopAutoClick();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CloudPosition cloudPosition = positions.get(position);
                cloudBusiness.cloudMovePreset(deviceId, cloudPosition.getPreset());
            }
        }, 2000);
    }

    @Override
    public void onCancelClick() {
        animateHideAddView();
    }

    /**
     * ********************************预置位添加弹框事件回调****************************************************
     */

    @Override
    public void onSubmitClick(String title) {
        int preset = 0;
        String path = "";
        if (positionEditing == null) {
            path = ShareUtil.getPresetImgPath(positionImageName);
            if (positions == null || positions.size() == 0) {
                preset = 1;
            } else {
                // 获取一个可用的preset
                List<Integer> presets = new ArrayList<Integer>();
                for (int i = 0; i < positions.size(); i++) {
                    presets.add(positions.get(i).getPreset());
                }
                for (int i = 1; i <= 6; i++) {
                    if (!presets.contains(i)) {
                        preset = i;
                        break;
                    }
                }
            }
            cloudBusiness.addPreset(deviceId, preset, title);
        } else {
            path = positionEditing.getImagePath();
            preset = positionEditing.getPreset();
        }

        Logger.d("presetNum:" + preset);
        // 保存
        CloudPositionWrapper.saveOrUpdatePosition(uid, deviceId, preset, title, path);
        positions = CloudPositionWrapper.getCloudPositions(uid, deviceId);
        cloudControllerView.updatePositions(positions);
        animateHideAddView();
    }

    /**
     * ********************************设置信息回调****************************************************
     */

    @Override
    public void onPowerCam(Business bus, boolean powerSwitched) {
        if (bus.isSuccess()) {
            IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
            business.openLiveMedia(deviceId);
        } else {
            viewController.toast(getString(R.string.open_fail));
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_DEV_START, null, getString(R.string.dev_power_off), getString(R.string.power_on_live));
        }
    }

    @Override
    public void onBitLevelChange(Business business, int bitLevel) {
        switch (business.getCode()) {
            case BusinessCode.SUCCESS:
                leve = bitLevel;
                switch (bitLevel) {
                    case 0:
                        viewController.toast(getString(R.string.low_definition_model));
                        break;
                    case 1:
                        viewController.toast(getString(R.string.high_definition_model));
                        break;
                    case 2:
                        viewController.toast(getString(R.string.super_definition_model));
                        break;
                    default:
                        break;
                }

                break;
            default:
                viewController.toast(getString(R.string.set_definition_fail));
                viewController.resetLevelOnError(leve);
                break;
        }
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business business) {
        if (type == CamSettingType.STATUS && business.isSuccess()) {
            CamStatus camStatus = business1.getCamStatus(deviceId);
            if (camStatus != null) {
                leve = camStatus.getBitlevel();
                viewController.setbitlevel(leve, false);
            }
        }
    }

    @Override
    public void onCapsule(CamCapsule capsule, Business bus) {
        if (bus.isSuccess()) {
            Logger.i("temperature:" + capsule.getTemperature() + "humidity:" + capsule.getHumidity());
            int humidity = (int) capsule.getHumidity();
            int temperature = (int) capsule.getTemperature();
            if (humidity >= 0 && temperature >= 1) {
                viewController.setCapsule(deviceId, temperature, humidity);
            } else {
                viewController.setCapsuleLayInvisible(true);
                capsuleTimer.cancel();
            }
        } else {
            capsuleTimer.cancel();
        }
    }

    /**
     * ********************************私有方法****************************************************
     */

    // 声音状态改变时调用
    private void onSoundStateChange() {
        if (viewController.isVoiceOn()) {
            buttonSound.setSelected(false);
            videoView.mute(0);// 声音开
        } else {
            buttonSound.setSelected(true);
            videoView.mute(1);// 声音关
        }
        preBusiness.setSoundStatus(deviceId, PreferenceConfig.SoundType.mineCam, viewController.isVoiceOn());
    }

    //判断百度播放地址无效 | 播放人数限制
    private void refreshBaiduStatusView(String playUrl) {
        if (ApiConfig.invalidPlayUrl(playUrl)) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getString(R.string.more_watch), null);
        }
    }

    private boolean refreshStatusView() {
        if (camlive == null) {
            return false;
        }
        if (Util.isNetworkConn(getActivity()) && !Util.isWifi(getActivity())) {
            viewStatus.showViewNotWifi();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewStatus.hideViewNotWifi();
                }
            }, 3000);
        }
        if (!Util.isNetworkAvailable(getActivity())) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.play_no_network), null);
            return false;
        } else if (camlive.isOffline()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getString(R.string.dev_off_line), null);
            return false;
        } else if (camlive.isPowerOff()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_DEV_START, null, getString(R.string.dev_power_off), getString(R.string.power_on_live));
            return false;
        } else {
            return true;
        }
    }

    // 设置按钮的禁用状态
    private void setEnabled(boolean isEnable) {
        isPlaying = isEnable;
        imageButtonSpeak.setEnabled(isEnable);
        imageButtonPrintScreen.setEnabled(isEnable);
        viewController.setButtonSpeakEnabled(isEnable);
        viewController.setButtonPrintScreenEnabled(isEnable);
        viewController.setIsIndicatorShow(isEnable);
        viewController.setSpeedTextShow(isEnable);
        viewController.setCheckDetailShow(isEnable);
        buttonLevel.setEnabled(isEnable);
        viewController.setButtonBitLevelEnabled(isEnable);
        if (isCloud) {
            buttonCloud.setEnabled(isEnable);
            viewController.setButtonCloudEnabled(isEnable);
        }
        buttonSound.setEnabled(isEnable);
        viewController.setVoiceEnable(isEnable);
    }

    //设置看录像按钮的禁用状态
    private void setButtonRecordEnabled(boolean isEnable) {
        imageButtonRecord.setEnabled(isEnable);
        viewController.setButtonRadioEnabled(isEnable);
    }

    private void initTalk() {
        if (camlive.getConnectType() == ConnectType.LINYANG) {
            talk.initLLYAudio(deviceId);
        }
        talk.setListener(this);
    }

    /**
     * 判断应用是否具有语音的权限
     *
     * @return
     */
    private boolean hasAudioPermission() {
        PackageManager pm = getActivity().getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.RECORD_AUDIO", AppConfig.PACKAGE_NAME));
        return permission;
    }

    /**
     * 动画显示添加预置位弹出框
     */
    private void animateShowAddView() {
        viewAddPrePositionBg.setVisibility(View.VISIBLE);
        String title = "";
        if (positionEditing != null) {
            title = positionEditing.getTitle();
        }
        addPrePositionView.setData(title);
        isAddWindowOpen = true;
        YoYo.with(Techniques.BounceInUp).duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                addPrePositionView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                addPrePositionView.editTextRequestFocus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(addPrePositionView);
    }

    /**
     * 动画隐藏添加预置位弹出框
     */
    private boolean ishiding;

    private void animateHideAddView() {
        if (!ishiding) {
            ishiding = true;
            YoYo.with(Techniques.SlideOutDown).duration(500).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    addPrePositionView.hideKeyboard();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    addPrePositionView.setVisibility(View.GONE);
                    viewAddPrePositionBg.setVisibility(View.GONE);
                    ishiding = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(addPrePositionView);
        }
    }

    /**
     * 动画隐藏预置位编辑框
     */
    private void animateHidePopView(final boolean isShowEditNext) {
        if (!ishiding) {
            ishiding = true;
            YoYo.with(Techniques.SlideOutDown).duration(300).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    viewAddPrePositionBg.setVisibility(View.GONE);
                    viewPopPositionEdit.setVisibility(View.GONE);
                    ishiding = false;
                    if (isShowEditNext) {
                        addPrePositionView.setImage(positionEditing.getImagePath());
                        addPrePositionView.hideKeyboard();
                        animateShowAddView();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(viewPopPositionEdit);
        }
    }

    /**
     * 禁用截屏按钮delayTime毫秒
     *
     * @param delayTime
     */
    private void printScreenTime(int delayTime) {
        imageButtonPrintScreen.setEnabled(false);
        viewController.setButtonPrintScreenEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageButtonPrintScreen.setEnabled(true);
                viewController.setButtonPrintScreenEnabled(true);
            }
        }, delayTime);
    }
}
