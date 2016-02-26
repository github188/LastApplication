package com.iermu.ui.fragment.test;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnBitLevelChangeListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamStatus;
import com.iermu.client.util.Logger;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.MineIermu.MineRecordFragment;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.PublicCamStatusView;
import com.lingyang.sdk.api.ILivePlayer;
import com.lingyang.sdk.api.VideoPlayerView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.Date;

/**
 * Created by zhoushaopei on 15/6/25.
 */


public class LyyLiveFragment extends BaseFragment implements View.OnClickListener
        , LyyLiveControllerView.OnControlListener
        //, OnLiveMediaListener
        //, IMediaPlayer.OnVideoSizeChangedListener
        //, IMediaPlayer.OnErrorListener
        , PublicCamStatusView.PublicStatusListener
        //, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnBufferingUpdateListener
        //, IMediaPlayer.OnInfoListener, IMediaPlayer.OnPreparedListener
        , View.OnTouchListener
        //, CmsTalk.CallBack
        , OnBitLevelChangeListener
        , ILivePlayer.OnPlayingListener {

    private static final String KEY_DEVICEID = "deviceId";

    @ViewInject(R.id.viewTop)
    RelativeLayout viewTop;
    @ViewInject(R.id.videoView)
    VideoPlayerView videoView;
    @ViewInject(R.id.viewController)
    LyyLiveControllerView viewController;
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

    ILivePlayer videoPlayer;
    private PublicCamStatusView viewStatus;
    private String deviceId;
    private CamLive camLive;

    // 画面移动有关参数
    private int marginLeft;
    private int touchX;
    private int offsetx;

    private String imagePath;

    ICamSettingBusiness business1;

    int leve;

    // 重试次数
//    private int userRetryNum;
//    private int autoRetryNum;
    private boolean isFirstLoad = true;
    private CamLive camlive;

    // 语音
//    CmsTalk talk;

    /**
     * 启动个人摄像头直播页面
     *
     * @param deviceId 设备ID
     * @return
     */
    public static Fragment actionInstance(String deviceId) {
        LyyLiveFragment fragment = new LyyLiveFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.my_live);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_lyylive, null);
        ViewHelper.inject(this, view);

        // 设置屏幕不锁定
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        business1 = ErmuBusiness.getCamSettingBusiness();
//        status = business1.getCamStatus(deviceId);
//        leve = (status != null) ? status.getBitlevel() : 0;

        videoPlayer = (ILivePlayer) videoView.getPlayer();
        videoPlayer.setOnPlayingListener(this);
        imageButtonSpeak.setOnTouchListener(this);
//        videoView.setOnCompletionListener(this);
//        videoView.setOnVideoSizeChangedListener(this);
//        videoView.setOnErrorListener(this);
//        videoView.setOnBufferingUpdateListener(this);
//        videoView.setOnInfoListener(this);
//        videoView.setOnPreparedListener(this);
//        videoView.setOnBufferingUpdateListener(this);
        setEnabled(false);
        viewTop.setOnTouchListener(this);
        viewController.setOnControlListener(this);
        viewController.setViewTop(viewTop);
        viewController.setViewVideo(videoView);
        viewController.setSpeakTouchListener(this);

        // 设置视频播放高度
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels;
        int screenHeight = screenWidth * 3 / 4;
        viewController.setHeight(screenHeight);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewTop.getLayoutParams();
        params.height = screenHeight;
        int videoWidth = screenHeight * 16 / 9;
        marginLeft = (videoWidth - screenWidth) / 2;
        viewController.setVideoMargin(marginLeft);
        RelativeLayout.LayoutParams paramsVideoView = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        paramsVideoView.setMargins(-marginLeft, 0, -marginLeft, 0);

        Bundle bundle = getArguments();
        business1 = ErmuBusiness.getCamSettingBusiness();
        deviceId = bundle.getString(KEY_DEVICEID);

        camlive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        CamStatus status = business1.getCamStatus(deviceId);
        leve = (status != null) ? status.getBitlevel() : 0;
        viewController.setbitlevel(leve);

//        talk = new CmsTalk();
//        initTalk();

        viewStatus = viewController.getViewStatus();
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camlive.getThumbnail(), getResources().getString(R.string.wait_connecting_cam), null);

        viewStatus.setListener(this);
        camLive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        setCommonActionBar(camlive.getDescription());
        viewController.setTitle(camLive.getDescription());
        long curDate = new Date().getTime();
        long endDate = camLive.getCvrEndTime();
        long offect = curDate - endDate;
        if (Integer.parseInt(camLive.getCvrDay()) > 0 || offect / 86400000 < 0) {
            setButtonRecordEnabled(true);
        } else {
            setButtonRecordEnabled(false);
        }
//        IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
//        business.registerListener(OnLiveMediaListener.class, this);
//        business.openLiveMedia(deviceId);

        ICamSettingBusiness business1 = ErmuBusiness.getCamSettingBusiness();
        business1.registerListener(OnBitLevelChangeListener.class, this);

//        videoPlayer.startLive(camLive.getConnectDid());
        refreshStatusView();
//        onLiveMediaChanged(deviceId);
        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewController.changeToPortrait();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            showActionBar();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewController.changeToLandscape();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideActionBar();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.i(">>> onHiddenChanged hidden:" + hidden);
        if (hidden) {
//            if (videoView != null) videoView.setVisibility(View.INVISIBLE);
            switchLivePlayer(false);
        } else {
            setCommonActionBar(camlive.getDescription());
//            if (videoView != null) videoView.setVisibility(View.VISIBLE);
            switchLivePlayer(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(">>> onResume");
        switchLivePlayer(true);
//        if (!viewStatus.isViewStatusShow()) {  //第二次加载页面
//            Logger.d("onResume reload");
//            viewStatus.setStatus(PublicCamStatusView.STATUS_LOADING);
//            viewStatus.setStatusText("重新加载中, 请稍后");
//
//            IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
//            business.openLiveMedia(deviceId);
//            if (videoView != null) videoView.start();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.i(">>> onPause");
        switchLivePlayer(false);
//        if (videoView != null) videoView.stopPlayback(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
//        IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
//        business.unRegisterListener(OnLiveMediaListener.class, this);
//        business.closeLiveMedia(deviceId);
        ICamSettingBusiness business1 = ErmuBusiness.getCamSettingBusiness();
        business1.unRegisterListener(OnBitLevelChangeListener.class, this);
//        if (videoView != null) videoView.stopPlayback(true);
//        videoPlayer.closeLive();

        // 清除屏幕不锁定参数
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @OnClick(value = {R.id.buttonCloud, R.id.buttonLevel, R.id.buttonSound, R.id.buttonFullScreen
            , R.id.imageButtonRecord, R.id.imageButtonSpeak, R.id.imageButtonPrintScreen})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCloud:
                break;
            case R.id.buttonLevel:
                viewController.changeRadioGroupState();
                break;
            case R.id.buttonSound:
                onSoundStateChange();
                viewController.changeVoiceState();
                break;
            case R.id.buttonFullScreen:
                videoView.setTranslationX(0);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case R.id.imageButtonRecord:
                Fragment fragment = MineRecordFragment.actionInstance(deviceId);
                addToBackStack(getActivity(), fragment);
                break;
            case R.id.imageButtonSpeak:
                break;
            case R.id.imageButtonPrintScreen:
                viewController.printScreen();
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
//
//    }
//
//    @Override
//    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i2) {
//        Logger.i("onError", "errorcode:" + i);
//        if (i == -10000) {
//            if (!ErmuApplication.isConnected()) {
//                if (userRetryNum >= 4) {
//                    userRetryNum = 0;
//                    viewStatus.showViewStatus();
//                    viewStatus.setStatus(PublicCamStatusView.STATUS_RESOLVE);
//                    viewStatus.setStatusText("刷了这么多次还上不了网？");
//                } else {
//                    viewStatus.showViewStatus();
//                    viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                    viewStatus.setStatusText("网络不给力，请检查网络后重试");
//                    userRetryNum++;
//                }
//            } else {
//                if (autoRetryNum >= 4) {
//                    autoRetryNum = 0;
//                    viewStatus.showViewStatus();
//                    viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                    viewStatus.setStatusText("加载失败，请重试");
//                } else {
//                    autoRetryNum++;
//                    viewStatus.showViewStatus();
//                    viewStatus.setStatus(PublicCamStatusView.STATUS_LOADING);
//                    viewStatus.setStatusText("重新加载第" + autoRetryNum + "次, 请稍后");
//                    IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
////                    business.reloadLiveMedia(deviceId);
//                }
//            }
//        }
//
//        switch (i) {
//            case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
//                Logger.i("Play Error:::", "MEDIA_ERROR_SERVER_DIED");
//                break;
//            case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
//                Logger.i("Play Error:::", "MEDIA_ERROR_UNKNOWN");
//                break;
//            default:
//                break;
//        }
//        return false;
//    }
//
//    @Override
//    public void onCompletion(IMediaPlayer iMediaPlayer) {
//        Logger.i("onCompletion " + iMediaPlayer.getCurrentPosition());
//    }
//
//    @Override
//    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
//        Logger.i("onBufferingUpdate " + i);
//    }
//
//    @Override
//    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i2) {
//        Logger.i("onInfo i=" + i + " i2=" + i2);
//        switch (i) {
//            case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
//                break;
//            case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
//                break;
//            case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
//                break;
//            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
//                break;
//        }
//        return false;
//    }
//
//    @Override
//    public void onPrepared(IMediaPlayer iMediaPlayer) {
//        if (!viewController.isVoiceOn()) {
////            videoView.mute(1);// 声音关
//        }
//        String token = ErmuBusiness.getBaiduAuthBusiness().getAccessToken();
//        talk.connectAudio(deviceId, token, camlive.getStreamId());
//        Logger.i("onPrepared " + iMediaPlayer.getCurrentPosition());
//        videoView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                viewStatus.hideViewStatus();
//                viewStatus.hideImageViewCache();
//                setEnabled(true);
//                userRetryNum = 0;
//                autoRetryNum = 0;
//            }
//        }, 1000);
//    }

    @Override
    public void onOutFullScreenClick(LyyLiveControllerView view) {
        videoView.setTranslationX(offsetx);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    @Override
    public void onCloudClick(LyyLiveControllerView view) {

    }

    @Override
    public void onPrintScreenClick(LyyLiveControllerView view) {
        viewController.printScreen();
    }

    @Override
    public void onLevelClick(LyyLiveControllerView view, int level, boolean isFirst) {
        switch (level) {
            case 0:
                buttonLevel.setImageResource(R.drawable.live_btn_fluent_normal_);
//                ErmuBusiness.getCamSettingBusiness().setBitLevel(deviceId, 0, isFirst);
                break;
            case 1:
                buttonLevel.setImageResource(R.drawable.live_btn_hd_normal_);
//                ErmuBusiness.getCamSettingBusiness().setBitLevel(deviceId, 1, isFirst);
                break;
            case 2:
                buttonLevel.setImageResource(R.drawable.live_btn_sd_normal_);
//                ErmuBusiness.getCamSettingBusiness().setBitLevel(deviceId, 2, isFirst);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRecordClick(LyyLiveControllerView view) {
        videoView.setTranslationX(offsetx);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Fragment fragment = MineRecordFragment.actionInstance(deviceId);
        addToBackStack(getActivity(), fragment);
    }

    @Override
    public void onViewCutClick(LyyLiveControllerView view) {
        ShareUtil.share(getActivity(), imagePath);
    }

    @Override
    public void onPrintScreenComplete(LyyLiveControllerView view, String imagePath, final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        this.imagePath = imagePath;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (viewController != null) viewController.setImage(bitmap);
            }
        });
    }

    @Override
    public void onSoundClick(LyyLiveControllerView view) {
        onSoundStateChange();
    }

    @Override
    public void onRefreshClick(PublicCamStatusView view) {
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, null, getResources().getString(R.string.repeat_connecting_cam), null);
        switchLivePlayer(true);
//        ErmuBusiness.getStreamMediaBusiness().openLiveMedia(deviceId);
    }

    @Override
    public void onButtonResolveClick(PublicCamStatusView view) {

    }

    @Override
    public void onButtonStartClick(PublicCamStatusView view) {

    }

    @Override
    public void onSwitchClick(PublicCamStatusView view) {

    }

    @Override
    public void onSwitchViewHide(PublicCamStatusView view) {

    }

//    @Override
//    public void onLiveMediaChanged(String devId) {
//        if (!devId.equals(deviceId)) return;
//        Logger.i("onLiveMediaChanged", "deviceId:" + devId);
//        LiveMedia media = ErmuBusiness.getStreamMediaBusiness().getLiveMedia(deviceId);
//        Logger.i("media", "isnull:" + (media == null));
//        if (media == null) {
//            if (isFirstLoad) {
//                isFirstLoad = false;
//                return;
//            }
//
//            if (!Util.isNetworkConn(getActivity())) {
//                if (userRetryNum >= 4) {
//                    userRetryNum = 0;
//                    viewStatus.showViewStatus();
//                    viewStatus.setStatus(PublicCamStatusView.STATUS_RESOLVE);
//                    viewStatus.setStatusText("刷了这么多次还上不了网？");
//                } else {
//                    viewStatus.showViewStatus();
//                    viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                    viewStatus.setStatusText("网络不给力，请检查网络后重试");
//                    userRetryNum++;
//                }
//            } else {
//                viewStatus.showViewStatus();
//                viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                viewStatus.setStatusText("加载失败，请重试");
//            }
//            return;
//        }
//
//        if (media instanceof BdLiveMedia) {
//            String playUrl = ((BdLiveMedia) media).getPlayUrl();
//            Logger.i("onLiveMediaChanged ", "playUrl:" + playUrl);
////            videoView.playVideo(playUrl, false);
//            refreshBaiduStatusView(playUrl);
//        } else if (media instanceof LyyLiveMedia) {
//            int status = ((LyyLiveMedia) media).getStatus();
//            String playUrl = ((LyyLiveMedia) media).getPlayUrl();
//            Logger.i("onLiveMediaChanged ", "playUrl:" + playUrl);
//            //0 离线
//            //1 准备就绪
//            //2 未就绪
//            //3 直播中
//            if (status == -1) {
//                viewStatus.setVisibility(View.VISIBLE);
//                viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT);
//                viewStatus.setStatusText("摄像机连接失败");
//                return;
//            } else if (((LyyLiveMedia) media).isOffline()) {
//                viewStatus.setVisibility(View.VISIBLE);
//                viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT);
//                viewStatus.setStatusText("摄像机离线了");
//                return;
//            }
//            if (status == 1 || status == 4) {
//                String hashId = ((LyyLiveMedia) media).getHashId();
//                videoPlayer.startLive(hashId);
////                videoView.playVideo(playUrl, false);
//            } else {
//                viewStatus.showViewStatus();
//                viewStatus.setStatus(PublicCamStatusView.STATUS_LOADING);
//                viewStatus.setStatusText("切换直播状态中...");
//                //ErmuBusiness.getStreamMediaBusiness().openLiveMedia(deviceId);
//            }
//        }
//    }

//    @Override
//    public void onConnectComplate(int ret) {
//        if (ret < 0) return;
//        // TODO 取消禁用对话按钮
//
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.viewTop) {
            viewController.hideLevel();
            if (viewController.isLandscape()) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewController.changeLanscapeControlHideState();
                }
            } else {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchX = (int) event.getRawX();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    int dx = (int) event.getRawX() - touchX;

                    offsetx = offsetx + dx;
                    if (offsetx > marginLeft) {
                        offsetx = marginLeft;
                    } else if (offsetx < -marginLeft) {
                        offsetx = -marginLeft;
                    }
                    videoView.setTranslationX(offsetx);

                    touchX = (int) event.getRawX();
                }
            }
        } else if (v.getId() == R.id.imageButtonSpeak || v.getId() == R.id.imageButtonSpeakCtrl) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                videoPlayer.startTalk();
//                talk.startAudio();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                videoPlayer.stopTalk();
//                talk.stopAudio();
//                imageButtonSpeak.setEnabled(false);
//                viewController.setButtonSpeakEnabled(false);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        imageButtonSpeak.setEnabled(true);
//                        viewController.setButtonSpeakEnabled(true);
//                    }
//                }, 1000);
            }
        }
        return false;
    }

    // 声音状态改变时调用
    private void onSoundStateChange() {
        if (viewController.isVoiceOn()) {
            buttonSound.setSelected(true);
//            videoView.mute(1);// 声音关
            videoPlayer.mute();
        } else {
            buttonSound.setSelected(false);
//            videoView.mute(0);// 声音开
            videoPlayer.demute();
        }
    }

//    //判断百度播放地址无效 | 播放人数限制
//    private void refreshBaiduStatusView(String playUrl) {
//        if (ApiConfig.invalidPlayUrl(playUrl)) {
//            viewStatus.showViewStatus();
//            viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT);
//            viewStatus.setStatusText("当前观看人数过多, 请稍后重试");
//        }
//    }

    private void refreshStatusView() {
        if (camlive == null) return;
        if (Util.isNetworkConn(getActivity()) && !Util.isWifi(getActivity())) {
            viewStatus.showViewNotWifi();
        }
        if (!Util.isNetworkAvailable(getActivity())) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getResources().getString(R.string.please_check_network), null);
        } else if (camlive.isOffline()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getResources().getString(R.string.cam_off_ine), null);
        } else if (camlive.isPowerOff()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getResources().getString(R.string.cam_turn_off), null);
        }
    }

    // 设置按钮的禁用状态
    private void setEnabled(boolean isEnable) {
        imageButtonSpeak.setEnabled(isEnable);
        imageButtonPrintScreen.setEnabled(isEnable);
        viewController.setButtonSpeakEnabled(isEnable);
        viewController.setButtonPrintScreenEnabled(isEnable);
        if (!isEnable) {
            buttonCloud.setEnabled(isEnable);
            viewController.setButtonCloudEnabled(isEnable);
        }
    }

    //设置看录像按钮的禁用状态
    private void setButtonRecordEnabled(boolean isEnable) {
        imageButtonRecord.setEnabled(isEnable);
        viewController.setButtonRadioEnabled(isEnable);
    }

//    private void initTalk() {
//        if (camlive.getConnectType() == ConnectType.LINYANG) {
//            talk.initLLYAudio(deviceId);
//        } else {
//            talk.setListener(this);
//        }
//    }

    @Override
    public void onPreparing() {
        Logger.i(">>>> onPreparing");
        setEnabled(true);
//        userRetryNum = 0;
//        autoRetryNum = 0;
    }

    @Override
    public void onDeviceOffline() {
        Logger.i(">>>> onOffLine");
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null,getResources().getString(R.string.cam_off_ine), null);
    }

    @Override
    public void onConnecting() {
        Logger.i(">>>> onConnecting");
    }

    @Override
    public void onLiving(long time) {
        Logger.i(">>>> onLiving " + time);
        viewStatus.hideViewStatus();
    }

    @Override
    public void onStoping() {
        Logger.i(">>>> onStoping");
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getResources().getString(R.string.disconnect_cam), null);
    }

    @Override
    public void onError(int i, String s) {
        Logger.i(">>>> onError i=" + i + " s=" + s);
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getResources().getString(R.string.connect_dev_err), null);
    }

    private void switchLivePlayer(boolean openLive) {
//        if (openLive) {
//            final String connectDid = camLive.getConnectDid();
//            if (videoView != null) videoView.resumeFromBackground();
//        } else {
//            if (videoView != null) videoView.pauseToBackground();
//            if (videoPlayer != null) videoPlayer.stop();
//        }
    }

    @Override
    public void onBitLevelChange(Business business, int bitLevel) {

    }
}
