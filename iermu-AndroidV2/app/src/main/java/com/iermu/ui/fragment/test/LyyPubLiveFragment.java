package com.iermu.ui.fragment.test;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IPubCamCommentBusiness;
import com.iermu.client.IStreamMediaBusiness;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.listener.OnCamCommentChangedListener;
import com.iermu.client.listener.OnCamLiveFindListener;
import com.iermu.client.listener.OnLiveMediaListener;
import com.iermu.client.model.CamComment;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.LiveMedia;
import com.iermu.client.util.Logger;
import com.iermu.ui.activity.BaiDuLoginActivity;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.adapter.PublicLiveCommentsAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.LiveControllerView;
import com.iermu.ui.view.PublicCamStatusView;
import com.lib.pulltorefreshview.PullToRefreshLayout;
import com.lib.pulltorefreshview.pullableview.PullableListView;
import com.lingyang.sdk.api.ILivePlayer;
import com.lingyang.sdk.api.VideoPlayerView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * 羚羊云公共摄像机直播
 * <p/>
 * Created by zhangxq on 15/7/21.
 */
public class LyyPubLiveFragment extends BaseFragment implements TextWatcher, LiveControllerView.OnControlListener,
        View.OnClickListener, PublicLiveCommentsAdapter.PublicCommentListListener,
        OnCamCommentChangedListener, PullToRefreshLayout.OnRefreshListener,
        OnLiveMediaListener, OnCamLiveFindListener, PublicCamStatusView.PublicStatusListener
        //, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener
        //, IMediaPlayer.OnPreparedListener
        , ILivePlayer.OnPlayingListener
        , View.OnTouchListener {

    private static final String KEY_SHARE_ID = "shareid";
    private static final String KEY_UK = "uk";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_FROM = "from";

    private boolean isopen = false;

    @ViewInject(R.id.videoView)
    VideoPlayerView videoView;
    @ViewInject(R.id.viewDanmu)
    IDanmakuView viewDanmu;
    @ViewInject(R.id.viewController)
    LiveControllerView viewController;
    @ViewInject(R.id.viewTop)
    RelativeLayout viewTop;

    @ViewInject(R.id.textViewDanmu)
    TextView textViewDanmu;
    @ViewInject(R.id.textViewSound)
    TextView textViewSound;
    @ViewInject(R.id.textViewShare)
    TextView textViewShare;
    @ViewInject(R.id.textViewStore)
    TextView textViewStore;

    @ViewInject(R.id.listView)
    PullableListView listView;
    @ViewInject(R.id.lvPullLayout)
    PullToRefreshLayout pullLayout;
    @ViewInject(R.id.editText)
    EditText editText;
    @ViewInject(R.id.buttonSend)
    Button buttonSend;
    @ViewInject(R.id.textViewContent)
    TextView textView;
    @ViewInject(R.id.linearLayoutBottom)
    View linearLayoutBottom;

    private PublicLiveCommentsAdapter adapter;
    private IPubCamCommentBusiness business;
    private Timer timer;
    private PublicCamStatusView viewStatus;

    private boolean isStoreOn = false;

    private String shareId;
    private String uk;
    private String deviceId;
    private CamLive camLive;
    private int favourNum;

    // 画面移动有关参数
    private int marginLeft;
    private int touchX;
    private int offsetx;

    // 重试次数
    private int userRetryNum;
    private int autoRetryNum;
    private boolean isFirstLoad = true;

//    static {
//        try {
    //System.loadLibrary("glrender");
    //System.loadLibrary("ffmpeg");
    //System.loadLibrary("liveplayer");
    //System.loadLibrary("audioels");
//        } catch (UnsatisfiedLinkError e) {
//            Logger.e("load library failed", e);
//        }
//    }

    private boolean playerInit = true;

    private BaseDanmakuParser mParser;
    ILivePlayer videoPlayer;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        hideActionBar();
    }

    /**
     * 实例化fragment
     *
     * @param shareId
     * @param uk
     * @return
     */
    public static LyyPubLiveFragment actionInstance(String deviceId, String shareId, String uk, boolean isFromPub) {
        LyyPubLiveFragment fragment = new LyyPubLiveFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SHARE_ID, shareId);
        bundle.putString(KEY_UK, uk);
        bundle.putString(KEY_DEVICE_ID, deviceId);
        bundle.putBoolean(KEY_FROM, isFromPub);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_lyypublive, null);
        ViewHelper.inject(this, view);
        // 设置屏幕不锁定
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoPlayer = (ILivePlayer) videoView.getPlayer();
        videoPlayer.setOnPlayingListener(this);
        // 设置控件
        viewController.setOnControlListener(this);
        textViewDanmu.setOnClickListener(this);
        textViewSound.setOnClickListener(this);
        textViewShare.setOnClickListener(this);
        textViewStore.setOnClickListener(this);
        buttonSend.setOnClickListener(this);

        viewTop.setOnTouchListener(this);
        viewController.setViewTop(viewTop);
        viewController.setBottomView(linearLayoutBottom);
//        viewController.setViewVideo(videoView);
//        videoView.setOnPreparedListener(this);
//        videoView.setOnVideoSizeChangedListener(this);
//        videoView.setOnErrorListener(this);
//        videoView.setOnCompletionListener(this);

        // 设置控制器高度
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

        viewStatus = viewController.getViewStatus();
        this.playerInit = true;

        business = ErmuBusiness.getPubCamCommentBusiness();
        business.registerListener(OnCamCommentChangedListener.class, this);
        business.registerListener(OnCamLiveFindListener.class, this);
        adapter = new PublicLiveCommentsAdapter(getActivity(), this, business.getUid());

        shareId = getArguments().getString(KEY_SHARE_ID);
        uk = getArguments().getString(KEY_UK);
        deviceId = getArguments().getString(KEY_DEVICE_ID);
        boolean isFromPub = getArguments().getBoolean(KEY_FROM);
        ErmuBusiness.getPubCamBusiness().viewCam(deviceId);
        if (isFromPub) {
            camLive = ErmuBusiness.getPubCamBusiness().getCamLive(shareId, uk);
        } else {
            camLive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        }

        if (camLive == null) {
            business.findCamLive(shareId, uk);
            textViewStore.setEnabled(false);
            textViewShare.setEnabled(false);
            viewController.setZanEnable(false);
        } else {
            refreshView();
        }
        if (!isStoreOn) {
            camLive.setStoreStatus(0);
            textViewStore.setText(getString(R.string.public_live_store));
        } else {
            camLive.setStoreStatus(1);
            textViewStore.setText(getString(R.string.public_live_over_store));
        }
//        String thumbnail = camLive.getThumbnail();
//        Picasso.with(getActivity()).load(TextUtils.isEmpty(thumbnail) ? "default" : thumbnail)
//                .transform(new Transformation() {
//                    @Override
//                    public Bitmap transform(Bitmap bitmap) {
//                        //if (videoView != null) {
//                        //    videoView.setVideoCover(bitmap);
//                        //}
//                        return bitmap;
//                    }
//                    @Override
//                    public String key() {
//                        return null;
//                    }
//                });

        IStreamMediaBusiness businessMedia = ErmuBusiness.getStreamMediaBusiness();
        businessMedia.registerListener(OnLiveMediaListener.class, this);
        businessMedia.openPubLiveMedia(deviceId, shareId, uk);

        business.syncNewCommentList(shareId, uk);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                business.refreshCommentList(shareId, uk);
            }
        }, 3000, 10000);

        listView.setAdapter(adapter);
        pullLayout.setOnRefreshListener(this);
        listView.setPullDown(true);
        editText.addTextChangedListener(this);
        refreshStatusView();
        return view;
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.textViewDanmu:
//                onDanmuStateChange();
//                viewController.changeDanmuState();
//                break;
//            case R.id.textViewSound:
//                onSoundStateChange();
//                viewController.changeVoiceState();
//                break;
//            case R.id.textViewShare:
//                textViewShare.setEnabled(false);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        textViewShare.setEnabled(true);
//                    }
//                }, 200);
//                share();
//                break;
//            case R.id.textViewStore:
//                if (ErmuBusiness.getAccountAuthBusiness().isLogin()) {
//                    if (Util.isNetworkConn(getActivity())) {
//                        if (isStoreOn) {
//                            textViewStore.setSelected(false);
//                            camLive.setStoreStatus(0);
//                            textViewStore.setText(R.string.public_live_store);
//                            storeCancel();
//                        } else {
//                            textViewStore.setSelected(true);
//                            camLive.setStoreStatus(1);
//                            textViewStore.setText(R.string.public_live_over_store);
//                            store();
//                        }
//                        isStoreOn = !isStoreOn;
//                    } else {
//                        Toast toast = Toast.makeText(getActivity(), R.string.network_error_wait, Toast.LENGTH_SHORT);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
//                    }
//                } else {
//                    BaiDuLoginActivity.actionStartBaiduLogin(getActivity());
//                }
//                break;
//            case R.id.buttonSend:
//                send();
//                break;
//            default:
//                break;
//
//        }
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.i("onHiddenChanged hidden:" + hidden);
        if (hidden) {
//            if (videoView != null) videoView.pause();
            switchLivePlayer(false);
        } else {
//            if (videoView != null) videoView.start();
            switchLivePlayer(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.i("onResume");
        //switchLivePlayer(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.i("onPause");
        switchLivePlayer(false);
//        if (videoView != null) videoView.stopPlayback(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        IPubCamCommentBusiness business = ErmuBusiness.getPubCamCommentBusiness();
        business.unRegisterListener(OnCamCommentChangedListener.class, this);
        business.unRegisterListener(OnCamLiveFindListener.class, this);
        IStreamMediaBusiness businessMedia = ErmuBusiness.getStreamMediaBusiness();
//        businessMedia.closeLiveMedia(deviceId);
        businessMedia.unRegisterListener(OnLiveMediaListener.class, this);
        business.setDanmuStudus(shareId);
        if (viewDanmu != null) viewDanmu.release();
        if (timer != null) timer.cancel();

//        if (videoView != null) {
//            videoView.stopPlayback(true);
//        }

        // 清除屏幕不锁定参数
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            buttonSend.setEnabled(true);
        } else {
            buttonSend.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 点击举报回调
     */
    @Override
    public void onReportClick() {
        WebActivity.actionStartWeb(getActivity(), WebActivity.PAFE_REPORT, deviceId, uk);
    }

    @Override
    public void onFullScreenClick(LiveControllerView view) {
        videoView.setTranslationX(0);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    @Override
    public void onOutFullScreenClick(LiveControllerView view) {
        videoView.setTranslationX(offsetx);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    @Override
    public void onDanmuClick(LiveControllerView view) {
        onDanmuStateChange();
    }

    @Override
    public void onSoundClick(LiveControllerView view) {
        onSoundStateChange();
    }

    @Override
    public void onBackClick(LiveControllerView view) {
        popBackStack();
    }

    @Override
    public void onFavourClick(LiveControllerView view) {
        business.favour(deviceId);
        favourNum++;
        viewController.setFavourNum(favourNum);
        camLive.setGoodNum(String.valueOf(favourNum));
    }

    @Override
    public void onRefreshClick(PublicCamStatusView view) {
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, null, getString(R.string.in_loading_please), null);
        //ErmuBusiness.getStreamMediaBusiness().openPubLiveMedia(deviceId, shareId, uk);
        switchLivePlayer(true);
    }

    @Override
    public void onButtonResolveClick(PublicCamStatusView view) {
        ErmuApplication.toast("查看如何解决");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewDanmu:
                onDanmuStateChange();
                viewController.changeDanmuState();
                break;
            case R.id.textViewSound:
                onSoundStateChange();
                viewController.changeVoiceState();
                break;
            case R.id.textViewShare:
                textViewShare.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewShare.setEnabled(true);
                    }
                }, 200);
                share();

                break;
            case R.id.textViewStore:
                if (ErmuBusiness.getAccountAuthBusiness().isLogin()) {
                    if (Util.isNetworkConn(getActivity())) {
                        if (isStoreOn) {
                            textViewStore.setSelected(false);
                            camLive.setStoreStatus(0);
                            textViewStore.setText(getString(R.string.public_live_store));
                            storeCancel();
                        } else {
                            textViewStore.setSelected(true);
                            camLive.setStoreStatus(1);
                            textViewStore.setText(getString(R.string.public_live_over_store));
                            store();
                        }
                        isStoreOn = !isStoreOn;
                    } else {
                        ErmuApplication.toast(getString(R.string.play_no_network));
                    }
                } else {
                    BaiDuLoginActivity.actionStartBaiduLogin(getActivity(), false);
                }
                break;
            case R.id.buttonSend:
                send();
                break;
            default:
                break;
        }
    }
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.textViewDanmu:
//                onDanmuStateChange();
//                viewController.changeDanmuState();
//                break;
//            case R.id.textViewSound:
//                onSoundStateChange();
//                viewController.changeVoiceState();
//                break;
//            case R.id.textViewShare:
//                share();
//                break;
//            case R.id.textViewStore:
//                if (isStoreOn) {
//                    textViewStore.setSelected(false);
//                    camLive.setStoreStatus(0);
//                    storeCancel();
//                } else {
//                    textViewStore.setSelected(true);
//                    camLive.setStoreStatus(1);
//                    store();
//                }
//                isStoreOn = !isStoreOn;
//                break;
//            case R.id.buttonSend:
//                send();
//                break;
//            default:
//                break;
//        }
//    }
//            public void onRefresh (PullToRefreshLayout pullToRefreshLayout){
//                pullLayout.refreshFinish(PullToRefreshLayout.DONE);
//            }
//
//            @Override
//            public void onLoadMore (PullToRefreshLayout pullToRefreshLayout){
//                business.syncOldCommentList(shareId, uk);
//            }


//            @Override
//            public boolean onError (IMediaPlayer iMediaPlayer,int i, int i2){
//                Logger.i("onError", "errorcode:" + i);
//                if (i == -10000) {
//                    if (!Util.isNetworkConn(getActivity())) {
//                        viewStatus.showViewStatus();
//                        viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                        viewStatus.setStatusText(getResources().getString(R.string.network_error_please_check));
//                    } else {
//                        if (autoRetryNum >= 2) {
//                            autoRetryNum = 0;
//                            viewStatus.showViewStatus();
//                            viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                            viewStatus.setStatusText(getResources().getString(R.string.load_error_repeat));
//                        } else {
//                            autoRetryNum++;
//                            viewStatus.showViewStatus();
//                            viewStatus.setStatus(PublicCamStatusView.STATUS_LOADING);
//                            viewStatus.setStatusText(getResources().getString(R.string.repeat_load_wait));
//                            ErmuBusiness.getStreamMediaBusiness().openPubLiveMedia(deviceId, shareId, uk);
//                        }
//                    }
//                }
//                return false;
//            }
//
//            @Override
//            public void onVideoSizeChanged (IMediaPlayer mp,int width, int height, int sar_num,
//            int sar_den){
//
//            }

//            @Override
//            public void onLiveMediaChanged (String devId){
//                if (!devId.equals(deviceId)) return;
//                Logger.i("onLƒiveMediaChanged", "deviceId:" + devId);
//                LiveMedia media = ErmuBusiness.getStreamMediaBusiness().getLiveMedia(deviceId);
//                viewController.hideView();
//                Logger.i("media", "isnull:" + (media == null));
//                if (media == null) {
//                    if (isFirstLoad) {
//                        isFirstLoad = false;
//                        return;
//                    }
//
//                    if (!Util.isNetworkConn(getActivity())) {
//                        viewStatus.showViewStatus();
//                        viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                        viewStatus.setStatusText(getResources().getString(R.string.network_error_please_check));
//                    } else {
//                        viewStatus.showViewStatus();
//                        viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                        viewStatus.setStatusText(getResources().getString(R.string.load_error_repeat));
//                    }
//                    return;
//                }
//
//                int connectType = media.getConnectType();
//                String playUrl = media.getPlayUrl();
//                Logger.i("onLiveMediaChanged ", "playUrl:" + playUrl);
//                videoView.playVideo(playUrl, false);
//                if (connectType == ConnectType.BAIDU) {
//                    refreshBaiduStatusView(playUrl);
//                }
//            }
//
//            @Override
//            public void onCompletion (IMediaPlayer iMediaPlayer){
//
//            }

//    @Override
//    public void onPrepared(IMediaPlayer iMediaPlayer) {
////                if (!viewController.isVoiceOn()) {
////                    videoView.mute(1);// 声音关
////                }
////                videoView.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        viewStatus.hideViewStatus();
////                        viewStatus.hideImageViewCache();
////                        startDanmu();
////                        autoRetryNum = 0;
////                    }
////                }, 1000);
////        if (!viewController.isVoiceOn()) {
////            videoView.mute(1);// 声音关
////        }
//        videoView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                viewStatus.hideViewStatus();
//                startDanmu();
//                autoRetryNum = 0;
//            }
//        }, 1000);
//    }

    // 记录移动距离，判断是否做控制器的显隐
//    private int movexBegin = 0;
//    private int moveyBegin = 0;
//    private int movexEnd = 0;
//    private int moveyEnd = 0;
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if (v.getId() == R.id.viewTop) {
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                movexEnd = (int) event.getRawX();
//                moveyEnd = (int) event.getRawY();
//                if (Math.abs(movexEnd - movexBegin) < Util.DensityUtil.dip2px(getActivity(), 20)
//                        && Math.abs(moveyEnd - moveyBegin) < Util.DensityUtil.dip2px(getActivity(), 20)) {
//                    viewController.changeControlHideState();
//                }
//            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                movexBegin = (int) event.getRawX();
//                moveyBegin = (int) event.getRawY();
//            }
//
//        }
//    }
//
//}

//            @Override
//            public boolean onError (IMediaPlayer iMediaPlayer,int i, int i2){
//                Logger.i("onError", "errorcode:" + i);
//                if (i == -10000) {
//                    if (!Util.isNetworkConn(getActivity())) {
//                        viewStatus.showViewStatus();
//                        viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                        viewStatus.setStatusText(getResources().getString(R.string.network_error_please_check));
//                    } else {
//                        if (autoRetryNum >= 2) {
//                            autoRetryNum = 0;
//                            viewStatus.showViewStatus();
//                            viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                            viewStatus.setStatusText(getResources().getString(R.string.load_error_repeat));
//                        } else {
//                            autoRetryNum++;
//                            viewStatus.showViewStatus();
//                            viewStatus.setStatus(PublicCamStatusView.STATUS_LOADING);
//                            viewStatus.setStatusText(getResources().getString(R.string.repeat_load_wait));
//                            ErmuBusiness.getStreamMediaBusiness().openPubLiveMedia(deviceId, shareId, uk);
//                        }
//                    }
//                }
//                return false;
//            }
//
//            @Override
//            public void onVideoSizeChanged (IMediaPlayer mp,int width, int height, int sar_num,
//            int sar_den){
//
//            }

//            @Override
//            public void onLiveMediaChanged (String devId){
//                if (!devId.equals(deviceId)) return;
//                Logger.i("onLƒiveMediaChanged", "deviceId:" + devId);
//                LiveMedia media = ErmuBusiness.getStreamMediaBusiness().getLiveMedia(deviceId);
//                viewController.hideView();
//                Logger.i("media", "isnull:" + (media == null));
//                if (media == null) {
//                    if (isFirstLoad) {
//                        isFirstLoad = false;
//                        return;
//                    }
//
//                    if (!Util.isNetworkConn(getActivity())) {
//                        viewStatus.showViewStatus();
//                        viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                        viewStatus.setStatusText(getResources().getString(R.string.network_error_please_check));
//                    } else {
//                        viewStatus.showViewStatus();
//                        viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//                        viewStatus.setStatusText(getResources().getString(R.string.load_error_repeat));
//                    }
//                    return;
//                }
//
//                int connectType = media.getConnectType();
//                String playUrl = media.getPlayUrl();
//                Logger.i("onLiveMediaChanged ", "playUrl:" + playUrl);
//                videoView.playVideo(playUrl, false);
//                if (connectType == ConnectType.BAIDU) {
//                    refreshBaiduStatusView(playUrl);
//                }
//            }
//
//            @Override
//            public void onCompletion (IMediaPlayer iMediaPlayer){
//
//            }

//    @Override
//    public void onPrepared(IMediaPlayer iMediaPlayer) {
////                if (!viewController.isVoiceOn()) {
////                    videoView.mute(1);// 声音关
////                }
////                videoView.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        viewStatus.hideViewStatus();
////                        viewStatus.hideImageViewCache();
////                        startDanmu();
////                        autoRetryNum = 0;
////                    }
////                }, 1000);
////        if (!viewController.isVoiceOn()) {
////            videoView.mute(1);// 声音关
////        }
//        videoView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                viewStatus.hideViewStatus();
//                startDanmu();
//                autoRetryNum = 0;
//            }
//        }, 1000);
//    }

    // 记录移动距离，判断是否做控制器的显隐
//    private int movexBegin = 0;
//    private int moveyBegin = 0;
//    private int movexEnd = 0;
//    private int moveyEnd = 0;
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if (v.getId() == R.id.viewTop) {
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                movexEnd = (int) event.getRawX();
//                moveyEnd = (int) event.getRawY();
//                if (Math.abs(movexEnd - movexBegin) < Util.DensityUtil.dip2px(getActivity(), 20)
//                        && Math.abs(moveyEnd - moveyBegin) < Util.DensityUtil.dip2px(getActivity(), 20)) {
//                    viewController.changeControlHideState();
//                }
//            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                movexBegin = (int) event.getRawX();
//                moveyBegin = (int) event.getRawY();
//            }
//
//        }
//    }
//
//}

    // 刷新部分页面数据
    private void refreshView() {
        deviceId = camLive.getDeviceId();
        textViewStore.setEnabled(true);
        textViewShare.setEnabled(true);
        viewController.setZanEnable(true);
        if (camLive.getGoodNum() == null || camLive.getGoodNum().length() == 0) {
            camLive.setGoodNum("0");
        }

        if (camLive.getStoreStatus() == 1) {
            isStoreOn = true;
            textViewStore.setSelected(true);
        }

        favourNum = Integer.parseInt(camLive.getGoodNum());
        viewController.setFavourNum(favourNum);

        adapter.setTitle(camLive.getDescription());
        viewController.setTitle(camLive.getDescription());
        adapter.notifyDataSetChanged();
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.in_loading_please), null);
        viewStatus.setListener(this);
    }

    // 弹幕状态改变时调用
    private void onDanmuStateChange() {
        if (viewController.isDanMuOn()) {
            textViewDanmu.setSelected(true);
            // 弹幕关
            viewDanmu.hide();
        } else {
            textViewDanmu.setSelected(false);
            // 弹幕开
            viewDanmu.show();
        }
    }

    // 声音状态改变时调用
    private void onSoundStateChange() {
        if (viewController.isVoiceOn()) {       // 声音关
            textViewSound.setSelected(true);
            videoPlayer.demute();
//            videoView.mute(1);
        } else {
            textViewSound.setSelected(false);   // 声音开
            videoPlayer.mute();
//            videoView.mute(0);
        }
    }

    // 收藏
    private void store() {
        business.store(shareId, uk);
    }

    // 取消收藏
    private void storeCancel() {
        business.unStore(shareId, uk, deviceId);
    }

    // 分享
    private void share() {
        String livePlay = getString(R.string.live_play);
        String liveShare = getString(R.string.share_public_content);
        String url = "http://www.iermu.com/view_share.php?shareid=" + shareId + "&uk=" + uk + "&share=" + 1;
        String imageUrl = camLive.getThumbnail();
        String description = (camLive!=null) ? camLive.getDescription() : "";
        String personNum = (camLive!=null) ? (camLive.getPersonNum() == 0 ? "" : camLive.getPersonNum()+"") : "";
        String title = String.format(livePlay, description);
        String content = String.format(liveShare, personNum);
        ShareUtil.share(getActivity(), title, url, imageUrl, content);
    }

    // 发送评论
    private void send() {
        if (!ErmuBusiness.getAccountAuthBusiness().isLogin()) {
            BaiDuLoginActivity.actionStartBaiduLogin(getActivity(), false);
            return;
        }
        if (!Util.isNetworkConn(getActivity())) {
            ErmuApplication.toast(getString(R.string.play_no_network));
        } else {
            String content = editText.getText().toString().trim();
            editText.setText("");
            business.sendComment(shareId, deviceId, content, 0);
            business.addDanmaku(viewDanmu, content);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);



            if (ErmuBusiness.getAccountAuthBusiness().isLogin()) {
                if (!Util.isNetworkConn(getActivity())) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.network_low), Toast.LENGTH_SHORT).show();
                } else {
                    String cont = editText.getText().toString().trim();
                    editText.setText("");
                    business.sendComment(shareId, deviceId, cont, 0);
                    business.addDanmaku(viewDanmu, content);
                    InputMethodManager im = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            } else {
                BaiDuLoginActivity.actionStartBaiduLogin(getActivity(), false);
            }
        }
    }

    private void refreshAdapter() {
        int count = business.getCount(shareId);
        List<CamComment> comments = business.getCommentList(shareId);
        adapter.notifyDataSetChanged(comments, count);
    }

    @Override
    public void onCamCommentChanged(boolean isNeedFinish) {
        if (isNeedFinish) {
            pullLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
        refreshAdapter();
    }

    @Override
    public void onFindCamLive(CamLive camLive) {
        if (camLive == null) {
            Logger.d("camlive is null");
            return;
        }
        this.camLive = camLive;

        refreshView();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pullLayout.refreshFinish(PullToRefreshLayout.DONE);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        business.syncOldCommentList(shareId, uk);
    }

//    @Override
//    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i2) {
//        Logger.i("onError", "errorcode:" + i);
//        if (i == -10000) {
//            if (!Util.isNetworkConn(getActivity())) {
//                if (userRetryNum >= 4) {
//                    userRetryNum = 0;
//                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_RESOLVE,null,"刷了这么多次还上不了网？",null);
////                    viewStatus.showViewStatus();
////                    viewStatus.setStatus(PublicCamStatusView.STATUS_RESOLVE);
////                    viewStatus.setStatusText("刷了这么多次还上不了网？");
//                } else {
//                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR,null,"网络不给力，请检查网络后重试",null);
////                    viewStatus.showViewStatus();
////                    viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
////                    viewStatus.setStatusText("网络不给力，请检查网络后重试");
//                    userRetryNum++;
//                }
//            } else {
//                if (autoRetryNum >= 4) {
//                    autoRetryNum = 0;
//                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR,null,"加载失败，请重试",null);
////                    viewStatus.showViewStatus();
////                    viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
////                    viewStatus.setStatusText("加载失败，请重试");
//                } else {
//                    autoRetryNum++;
//                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING,null,"重新加载第" + autoRetryNum + "次, 请稍后",null);
////                    viewStatus.showViewStatus();
////                    viewStatus.setStatus(PublicCamStatusView.STATUS_LOADING);
////                    viewStatus.setStatusText("重新加载第" + autoRetryNum + "次, 请稍后");
//                    ErmuBusiness.getStreamMediaBusiness().openPubLiveMedia(deviceId, shareId, uk);
//                }
//            }
//        }
//        return false;
//    }

//    @Override
//    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
//
//    }

    @Override
    public void onLiveMediaChanged(String devId) {
        if (!devId.equals(deviceId)) return;
        Logger.i("onLiveMediaChanged", "deviceId:" + devId);
        LiveMedia media = ErmuBusiness.getStreamMediaBusiness().getLiveMedia(deviceId);
        Logger.i("media", "isnull:" + (media == null));
        if (media == null) {
            if (isFirstLoad) {
                isFirstLoad = false;
                return;
            }

            if (!Util.isNetworkConn(getActivity())) {
                if (userRetryNum >= 4) {
                    userRetryNum = 0;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_RESOLVE, null, "刷了这么多次还上不了网？", null);
                } else {
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, "网络不给力，请检查网络后重试", null);
                    userRetryNum++;
                }
            } else {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, "加载失败，请重试", null);
            }
            return;
        }

        String playUrl = media.getPlayUrl();
        Logger.i("onLiveMediaChanged ", "playUrl:" + playUrl);
        switchLivePlayer(true);
    }

    private void startDanmu() {
        if (!isopen) {
            // 弹幕
            DanmakuGlobalConfig config = DanmakuGlobalConfig.DEFAULT;
            config.setScrollSpeedFactor(1.5f);
            config.setDanmakuStyle(DanmakuGlobalConfig.DANMAKU_STYLE_DEFAULT, 4).setDuplicateMergingEnabled(false).setMaximumVisibleSizeInScreen(80);
            if (viewDanmu != null) {
                mParser = business.getDanmuInputStream(shareId);
                viewDanmu.setCallback(new DrawHandler.Callback() {
                    @Override
                    public void updateTimer(DanmakuTimer timer) {

                    }

                    @Override
                    public void prepared() {
                        viewDanmu.start();
                        isopen = true;
                        business.beginDanmu(viewDanmu, shareId);
                    }
                });
                viewDanmu.prepare(mParser);
                isopen = true;
                viewDanmu.enableDanmakuDrawingCache(true);
            }
        }
    }

//    @Override
//    public void onCompletion(IMediaPlayer iMediaPlayer) {
//
//    }
//
//    @Override
//    public void onPrepared(IMediaPlayer iMediaPlayer) {
//        if (!viewController.isVoiceOn()) {
//            videoView.mute(1);// 声音关
//        }
//        videoView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                viewStatus.hideViewStatus();
//                viewStatus.hideImageViewCache();
//                startDanmu();
//                userRetryNum = 0;
//                autoRetryNum = 0;
//            }
//        }, 1000);
//    }

// 记录移动距离，判断是否做控制器的显隐
private int movexBegin = 0;
private int moveyBegin = 0;
private int movexEnd = 0;
private int moveyEnd = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.viewTop) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (Math.abs(movexEnd - movexBegin) < 20 && Math.abs(moveyEnd - moveyBegin) < 20) {
                    viewController.changeControlHideState();
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchX = (int) event.getRawX();
                movexBegin = (int) event.getRawX();
                moveyBegin = (int) event.getRawY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                movexEnd = (int) event.getRawX();
                moveyEnd = (int) event.getRawY();
                if (!viewController.isLandscape()) {
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
        }
        return false;
    }

    //判断百度播放地址无效 | 播放人数限制
    private void refreshBaiduStatusView(String playUrl) {
        if (ApiConfig.invalidPlayUrl(playUrl)) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getString(R.string.more_watch), null);
        }
    }

    private void refreshStatusView() {
        if (camLive == null) return;
        if (Util.isNetworkConn(getActivity()) && !Util.isWifi(getActivity())) {
            viewStatus.showViewNotWifi();
        }
        if (!Util.isNetworkAvailable(getActivity())) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.play_no_network), null);
        } else if (camLive.isOffline()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getString(R.string.dev_off_line), null);
        } else if (camLive.isPowerOff()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getString(R.string.dev_power_off), null);
        }
    }

    @Override
    public void onPreparing() {
        Logger.i(">>>> onPreparing");
    }

    @Override
    public void onDeviceOffline() {
        Logger.i(">>>> onOffLine");
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, "摄像机离线了", null);
    }

    @Override
    public void onConnecting() {
        Logger.i(">>>> onConnecting");
        startDanmu();
    }

    @Override
    public void onLiving(long time) {
        Logger.i(">>>> onLiving " + time);
        viewStatus.hideViewStatus();
    }

    @Override
    public void onStoping() {
        Logger.i(">>>> onStoping");
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, "断开摄像机连接中", null);
    }

    @Override
    public void onError(int i, String s) {
        Logger.i(">>>> onError i=" + i + " s=" + s);
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, "连接摄像机失败", null);
    }

    private void switchLivePlayer(boolean openLive) {
        if (openLive) {
            LiveMedia media = ErmuBusiness.getStreamMediaBusiness().getLiveMedia(deviceId);
            if (media == null) return;
            String connectCid = camLive.getConnectCid();
            final String playUrl = media.getPlayUrl();
            if (videoView != null) videoView.resumeFromBackground();
            Logger.i(">>>>begin startLive connectCid=" + connectCid + " url=" + playUrl);
            videoPlayer.play(connectCid, playUrl);
            Logger.i(">>>>end startLive i=" + connectCid + " url=" + playUrl);
        } else if (camLive.isOffline()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT,null,getResources().getString(R.string.cam_off_ine),null);
//            viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT);
//            viewStatus.setStatusText(getResources().getString(R.string.cam_off_ine));
        } else if (camLive.isPowerOff()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT,null,getResources().getString(R.string.cam_turn_off),null);
//            viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT);
//            viewStatus.setStatusText(getResources().getString(R.string.cam_turn_off));
        }else {
            if (videoView != null) videoView.pauseToBackground();
            if (videoPlayer != null) videoPlayer.stop();
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getResources().getString(R.string.please_check_network_low), null);
//            viewStatus.setStatus(PublicCamStatusView.STATUS_ERROR);
//            viewStatus.setStatusText(getResources().getString(R.string.please_check_network_low));
        }
    }
}
