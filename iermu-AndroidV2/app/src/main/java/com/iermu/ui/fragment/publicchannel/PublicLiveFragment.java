package com.iermu.ui.fragment.publicchannel;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cms.media.player.IMediaPlayer;
import com.cms.media.widget.VideoView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.activity.BaiDuLoginActivity;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.adapter.PublicLiveCommentsAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.LiveControllerView;
import com.iermu.ui.view.PublicCamStatusView;
import com.iermu.ui.view.ShareImgpathDialog;
import com.lib.pulltorefreshview.PullToRefreshLayout;
import com.lib.pulltorefreshview.pullableview.PullableListView;
import com.mob.tools.utils.UIHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
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
public class PublicLiveFragment extends BaseFragment implements TextWatcher, LiveControllerView.OnControlListener,
        View.OnClickListener, PublicLiveCommentsAdapter.PublicCommentListListener,
        OnCamCommentChangedListener, PullToRefreshLayout.OnRefreshListener,
        OnLiveMediaListener, OnCamLiveFindListener, PublicCamStatusView.PublicStatusListener
        , IMediaPlayer.OnErrorListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener
        , View.OnTouchListener, PlatformActionListener, Handler.Callback {

    private static final String KEY_SHARE_ID = "shareid";
    private static final String KEY_UK = "uk";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_FROM = "from";
    private static final int MSG_TOAST = 1;
    private static final int MSG_ACTION_CCALLBACK = 2;
    private static final int MSG_CANCEL_NOTIFY = 3;

    private boolean isopen = false;

    @ViewInject(R.id.videoView)
    VideoView videoView;
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
    @ViewInject(R.id.share_layout)
    RelativeLayout mShareLay;
    @ViewInject(R.id.share_trans)
    View mShareTrans;
    @ViewInject(R.id.share_wechat)
    Button mShareWx;
    @ViewInject(R.id.share_moments)
    Button mShareMoments;
    @ViewInject(R.id.share_cancel)
    Button mShareCancel;
    @ViewInject(R.id.share_type)
    RelativeLayout mShareType;
    private PublicCamStatusView viewStatus;

    private PublicLiveCommentsAdapter adapter;
    private IPubCamCommentBusiness business;
    private String shareId;
    private String uk;
    private String deviceId;
    private CamLive camLive;
    private int favourNum; // 点赞个数
    private boolean isStoreOn = false; // 是否已收藏
    private Timer refreshCommentTimer; // 用来刷新评论列表

    // 重试次数
    private int autoRetryNum;

    // 弹幕
    private BaseDanmakuParser mParser;
    //分享
    private ShareImgpathDialog shareDialog;
    private boolean isShareVisible = false;

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
    private static boolean isOpened;

    public static PublicLiveFragment actionInstance(String deviceId, String shareId, String uk, boolean isFromPub) {
        if (!isOpened) {
            isOpened = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOpened = false;
                }
            }, 1000);

            PublicLiveFragment fragment = new PublicLiveFragment();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_SHARE_ID, shareId);
            bundle.putString(KEY_UK, uk);
            bundle.putString(KEY_DEVICE_ID, deviceId);
            bundle.putBoolean(KEY_FROM, isFromPub);
            fragment.setArguments(bundle);
            return fragment;
        } else {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_lyy_live_public, null);
        ViewHelper.inject(this, view);
        Logger.i(">>>> onCreateView " + System.currentTimeMillis());
        // 初始化全局参数
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
        viewController.hideView();
        // 设置cover图
        if (camLive.getThumbnail() != null && camLive.getThumbnail().length() > 0) {
            Picasso.with(getActivity()).load(camLive.getThumbnail()).into(target);
        }

        // 设置屏幕不锁定
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }
        }, 1000);

        // 设置监听
        viewController.setOnControlListener(this);
        textViewDanmu.setOnClickListener(this);
        textViewSound.setOnClickListener(this);
        textViewShare.setOnClickListener(this);
        textViewStore.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        viewTop.setOnTouchListener(this);
        mShareWx.setOnClickListener(this);
        mShareMoments.setOnClickListener(this);
        mShareCancel.setOnClickListener(this);
        mShareTrans.setOnTouchListener(this);
        viewController.setViewTop(viewTop);
        viewController.setBottomView(linearLayoutBottom);
        viewController.setViewVideo(videoView);
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnVideoSizeChangedListener(this);//增加空的监听回调.解决画面抖动问题
        viewStatus = viewController.getViewStatus();

        // 设置控制器高度和广角指示器移动范围
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels;
        int screenHeight = screenWidth * 3 / 4;
        viewController.setHeight(screenHeight);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewTop.getLayoutParams();
        params.height = screenHeight;
        int videoWidth = screenHeight * 16 / 9;
        int marginLeft = (videoWidth - screenWidth) / 2;
        viewController.setVideoMargin(marginLeft);

        // 初始化评论
        business = ErmuBusiness.getPubCamCommentBusiness();
        business.registerListener(OnCamCommentChangedListener.class, this);
        business.registerListener(OnCamLiveFindListener.class, this);
        adapter = new PublicLiveCommentsAdapter(getActivity(), this, business.getUid());
        business.syncNewCommentList(shareId, uk);
        refreshCommentTimer = new Timer();
        refreshCommentTimer.schedule(new TimerTask() {
            public void run() {
                business.refreshCommentList(shareId, uk);
            }
        }, 3000, 10000);
        listView.setAdapter(adapter);
        pullLayout.setOnRefreshListener(this);
        listView.setPullDown(true);
        editText.addTextChangedListener(this);
        String language = LanguageUtil.language();
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(language.equals("zh")? 40 : 140) });

        // 初始化按钮状态
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

        // 播放直播
        IStreamMediaBusiness businessMedia = ErmuBusiness.getStreamMediaBusiness();
        businessMedia.registerListener(OnLiveMediaListener.class, this);

        // 初始化状态转换器
        refreshStatusView();
//        timer = new Timer();
//        timer.schedule(task, 0, 1500);
        return view;
    }


    private long m_iStartTime = 0;
    private long m_iOpenTime = 0;
//    Timer timer;
//    TimerTask task = new TimerTask() {
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
//            Logger.i(strTip);
//            videoView.post(new Runnable() {
//                @Override
//                public void run() {
//                    viewController.getSpeed(strTip);
//                }
//            });
//        }
//    };

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
                            textViewStore.setText(R.string.public_live_store);
                            storeCancel();
                        } else {
                            textViewStore.setSelected(true);
                            camLive.setStoreStatus(1);
                            textViewStore.setText(R.string.public_live_over_store);
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
            default:
                break;

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideActionBar();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewController.changeOutOfFullScreen();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            viewController.changeToFullScreen();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (isShareVisible) animationOut();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.i("onHiddenChanged hidden:" + hidden);
        if (bitmapCover != null && !bitmapCover.isRecycled()) {
            videoView.setVideoCover(bitmapCover);
        }
        if (hidden) {
            if (videoView != null) videoView.pause();
        } else {
            if (videoView != null) videoView.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.i("onResume " + System.currentTimeMillis());
        if (bitmapCover != null && !bitmapCover.isRecycled()) {
            videoView.setVideoCover(bitmapCover);
        }
        ErmuBusiness.getStreamMediaBusiness().openPubLiveMedia(deviceId, shareId, uk);
        if (!viewStatus.isViewStatusShow()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.in_loading_please), null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.i("onPause");
        ErmuBusiness.getStreamMediaBusiness().closeLiveMedia(deviceId);
        if (videoView != null) videoView.stopPlayback(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isShareVisible) {
                animationOut();
                return true;
            }

            if (viewController.isLandscape()) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (task != null) task.cancel();
//        if (timer != null) timer.cancel();
        //Picasso.with(getActivity()).cancelRequest(target);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        IPubCamCommentBusiness business = ErmuBusiness.getPubCamCommentBusiness();
        business.unRegisterListener(OnCamCommentChangedListener.class, this);
        business.unRegisterListener(OnCamLiveFindListener.class, this);
        IStreamMediaBusiness businessMedia = ErmuBusiness.getStreamMediaBusiness();
        businessMedia.closeLiveMedia(deviceId);
        businessMedia.unRegisterListener(OnLiveMediaListener.class, this);
        business.setDanmuStudus(shareId);
        if (viewDanmu != null) viewDanmu.release();
        if (refreshCommentTimer != null) refreshCommentTimer.cancel();

        if (videoView != null) {
            videoView.stopPlayback(true);
        }

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
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    @Override
    public void onOutFullScreenClick(LiveControllerView view) {
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
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.in_loading_please), null);
        ErmuBusiness.getStreamMediaBusiness().openPubLiveMedia(deviceId, shareId, uk);
    }

    @Override
    public void onButtonResolveClick(PublicCamStatusView view) {
        WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_SOLVE);
    }

    @Override
    public void onButtonStartClick(PublicCamStatusView view) {
        // 开机，公共频道可忽略
    }

    @Override
    public void onSwitchClick(PublicCamStatusView view) {

    }

    @Override
    public void onSwitchViewHide(PublicCamStatusView view) {

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

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i2) {
        Logger.i("onError", "errorcode:" + i);
        if (i == -10000) {
            if (!Util.isNetworkConn(getActivity())) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.play_no_network), null);
            } else {
                if (autoRetryNum >= 2) {
                    autoRetryNum = 0;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.load_fail), null);
                } else {
                    autoRetryNum++;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.in_loading_please), null);
                    ErmuBusiness.getStreamMediaBusiness().openPubLiveMedia(deviceId, shareId, uk);
                }
            }
        }
        return false;
    }

    @Override
    public void onLiveMediaChanged(String devId) {
        if (!devId.equals(deviceId)) return;
        Logger.i("onLƒiveMediaChanged", "deviceId:" + devId);
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
        boolean isLanRtmp = media.isLanRtmp();

        if (connectType == ConnectType.LINYANG) {
            ErmuBusiness.getStatisticsBusiness().statStartPlay(deviceId, connectType, connectRet, status);
        }
        Logger.i("onLiveMediaChanged ", "playUrl:" + playUrl);
        if (connectType == ConnectType.LINYANG && !media.isConnected()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR_WITH_SUB_TEXT, null, getString(R.string.conn_dev_fail), null);
            return;
        }
        m_iStartTime = System.currentTimeMillis();
        videoView.bufferON(false);
        videoView.setDelayMS(500);
        videoView.playVideo(playUrl, false);
        videoView.setDeviceID(devId); //在playvideo接口后调用这个接口，退出播放后会向后台服务器上传播放日志
        if (connectType == ConnectType.BAIDU) {
            refreshBaiduStatusView(playUrl);
        }
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        Logger.i(">>>> onPrepared" + " " + System.currentTimeMillis());
        if (!viewController.isVoiceOn()) {
            videoView.mute(1);// 声音关
        }
//        videoView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        viewStatus.hideViewStatus();
        startDanmu();
        autoRetryNum = 0;
//            }
//        }, 1000);
        //TODO 隐藏进度条
        //当前时间为:直播播放时间
        //1.计算直播播放时间-直播开始时间=直播打开时间
        m_iOpenTime = System.currentTimeMillis() - m_iStartTime;
        Logger.i("开流时间: " + m_iOpenTime);
    }

    // 记录移动距离，判断是否做控制器的显隐
    private int movexBegin = 0;
    private int moveyBegin = 0;
    private int movexEnd = 0;
    private int moveyEnd = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.viewTop) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                movexEnd = (int) event.getRawX();
                moveyEnd = (int) event.getRawY();
                if (Math.abs(movexEnd - movexBegin) < Util.DensityUtil.dip2px(getActivity(), 20)
                        && Math.abs(moveyEnd - moveyBegin) < Util.DensityUtil.dip2px(getActivity(), 20)) {
                    viewController.changeControlHideState();
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                movexBegin = (int) event.getRawX();
                moveyBegin = (int) event.getRawY();
            }

            viewController.onTopTouch(event);
        } else if (v.getId() == R.id.share_trans) {
            int y = (int) event.getY();
            int top = mShareType.getTop();
            if (y < top && isShareVisible) animationOut();
            return false;
        }
        return false;
    }

    // 刷新部分页面数据
    private void refreshView() {
        deviceId = camLive.getDeviceId();
        textViewStore.setEnabled(true);
        textViewShare.setEnabled(true);
        viewController.setZanEnable(true);
        if (camLive.getGoodNum() == null || camLive.getGoodNum().length() == 0) {
            camLive.setGoodNum("0");
        }

        if (camLive.getStoreStatus() == 1 && ErmuBusiness.getAccountAuthBusiness().isLogin()) {
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
            videoView.mute(1);
        } else {
            textViewSound.setSelected(false);   // 声音开
            videoView.mute(0);
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
        aniIn();
//        String mimeShare = getString(R.string.mime_share);
//        String shareYou = getString(R.string.live_share_you);
//        String title = getString(R.string.on_live_enjoyable);
//        String url = "http://www.iermu.com/view_share.php?shareid=" + shareId + "&uk=" + uk + "&share=" + 1;
//        String imageUrl = camLive.getThumbnail();
//        String content = mimeShare + camLive.getDescription() + shareYou;
//        ShareUtil.share(getActivity(), title, url, imageUrl, content);
//
//        animationIn(title, url, imageUrl, content);
    }

    private void aniIn() {
        isShareVisible = true;
        mShareType.setVisibility(View.VISIBLE);
        mShareTrans.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp).duration(300).playOn(mShareType);
    }

    private void animationOut() {
        isShareVisible = false;
        mShareTrans.setVisibility(View.GONE);
        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(mShareType);
    }

    private void animationIn(String title, String url, String imageUrl, String content) {
        shareDialog = new ShareImgpathDialog(getActivity(), R.style.custom_dialog, title, url, imageUrl, content);
        Window window = shareDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.dialogAnim_style);  //添加动画
        window.setBackgroundDrawableResource(R.drawable.custom_bg);
        shareDialog.show();
    }

    private Platform.ShareParams shareParams() {
        String livePlay = getString(R.string.live_play);
        String liveShare = getString(R.string.share_public_content);
        String url = "http://www.iermu.com/view_share.php?shareid=" + shareId + "&uk=" + uk + "&share=" + 1;
        String imageUrl = camLive.getThumbnail();
        int personNum = (camLive!=null) ? (camLive.getPersonNum() == 0 ? 1 : camLive.getPersonNum()) : 1;
//        ShareUtil.share(getActivity(), title, url, imageUrl, content);

        Platform.ShareParams wechat = new Platform.ShareParams();
            wechat.setTitle(String.format(livePlay, camLive.getDescription()));
            wechat.setText(String.format(liveShare, personNum));
            wechat.setUrl(url);
            wechat.setImageUrl(imageUrl);
            wechat.setShareType(Platform.SHARE_WEBPAGE);
            return wechat;
    }


    // 发送评论
    private void send() {
        if (ErmuBusiness.getAccountAuthBusiness().isLogin()) {
            if (!Util.isNetworkConn(getActivity())) {
                ErmuApplication.toast(getString(R.string.play_no_network));
            } else {
                String content = editText.getText().toString().trim();
                editText.setText("");
                business.sendComment(shareId, deviceId, content, 0);
                business.addDanmaku(viewDanmu, content);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        } else {
            BaiDuLoginActivity.actionStartBaiduLogin(getActivity(), false);
        }
    }

    private void refreshAdapter() {
        int count = business.getCount(shareId);
        List<CamComment> comments = business.getCommentList(shareId);
        adapter.notifyDataSetChanged(comments, count);
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

    //判断百度播放地址无效 | 播放人数限制
    private void refreshBaiduStatusView(String playUrl) {
        if (ApiConfig.invalidPlayUrl(playUrl)) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.more_watch), null);
        }
    }

    private void refreshStatusView() {
        if (camLive == null) return;
        if (Util.isNetworkConn(getActivity()) && !Util.isWifi(getActivity())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewStatus.hideViewNotWifi();
                }
            }, 3000);
        }
        if (!Util.isNetworkAvailable(getActivity())) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.please_check_network), null);
        } else if (camLive.isOffline()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.cam_off_ine), null);
        } else if (camLive.isPowerOff()) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.cam_turn_off), null);
        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i2, int i3, int i4) {

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
