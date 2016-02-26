package com.iermu.ui.fragment.MineIermu;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cms.media.player.IMediaPlayer;
import com.cms.media.widget.VideoView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMineRecordBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.config.PreferenceConfig;
import com.iermu.client.listener.OnNasRecordChangedListener;
import com.iermu.client.listener.OnOpenPlayCardRecordListener;
import com.iermu.client.model.CamDate;
import com.iermu.client.model.CamLive;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.Logger;
import com.iermu.lan.model.CamRecord;
import com.iermu.lan.model.ErrorCode;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.adapter.RecordDateGridAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CardRecordLiveControllerView;
import com.iermu.ui.view.ExpandTimeLineView;
import com.iermu.ui.view.PublicCamStatusView;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zsj on 15/12/31.
 */
public class MineNasRecordFragment extends BaseFragment implements View.OnClickListener,
        CardRecordLiveControllerView.OnControlListener, AdapterView.OnItemClickListener,
        PublicCamStatusView.PublicStatusListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnPreparedListener,
        View.OnTouchListener, OnOpenPlayCardRecordListener,
        ExpandTimeLineView.TimeLineScrollListener, OnNasRecordChangedListener {

    private static final String KEY_DEVICEID = "deviceId";

    @ViewInject(R.id.viewTop)
    RelativeLayout viewTop;
    @ViewInject(R.id.videoView)
    VideoView videoView;
    @ViewInject(R.id.viewController)
    CardRecordLiveControllerView viewController;
    @ViewInject(R.id.buttonPlay)
    ImageButton buttonPlay;
    @ViewInject(R.id.buttonCamera)
    ImageButton imageButtonPrintScreen;
    @ViewInject(R.id.buttonSound)
    ImageButton buttonSound;
    @ViewInject(R.id.viewTimeLineParent)
    View viewTimeLineParent;
    @ViewInject(R.id.timeLineView)
    ExpandTimeLineView timeLineView;
    @ViewInject(R.id.viewHorizontal)
    LinearLayout viewHorizontal;
    @ViewInject(R.id.viewTimelist)
    View viewTimeList;
    @ViewInject(R.id.viewDateSelectBg)
    View viewDateSelectBg;
    @ViewInject(R.id.textViewTime)
    TextView textViewTime;
    @ViewInject(R.id.gridViewDates)
    GridView gridViewDates;
    @ViewInject(R.id.textViewCardMsg)
    TextView textViewCardMsg;
    private PublicCamStatusView viewStatus;

    // 数据适配器
    private RecordDateGridAdapter gridAdapter;
    private RecordDateGridAdapter gridAdapterCtrl;

    private CamLive camLive;
    private String deviceId;
    private IMineRecordBusiness business;
    private IPreferenceBusiness preBusiness;
    private long imageTime; // 截图时间

    // 播放相关参数
    private int currentPlayingTime; // 记录当前播放的时间秒
    private int currentPlayingStartTime; // 记录开始播放时间
    private int positionPlaying; // 记录当前录像的位置
    private Timer currentTimeTimer; // 循环获取当前播放的时间
    private boolean isOnScroll; // 记录是否正在滚动时间轴
    private int screenWidth; // 屏幕宽度
    private int screenHeight; // 屏幕高度
    private boolean isPlayed = false; // 记录是否初始化的时候播放了
    private int nextStartTime; // 记录录像长度大于15分钟时的下一次播放开始时间
    private int playPosition; // 记录当前录像播放时长（保证时间不回退）

    // 状态转换有关参数
    private int autoRetryNum;

    // 卡录有关参数
    private int findCardRecordNum; // 获取卡录视频列表重试次数，不大于3次
    private Dialog dialogCardInfo; // 卡信息弹出框
    private IKnowCallBack listener; // 我知道了按钮监听器

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

    /**
     * 带横竖屏切换，打开录像页面
     *
     * @param deviceId
     * @return
     */
    public static Fragment actionInstance(String deviceId) {
        MineNasRecordFragment fragment = new MineNasRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_mine_card_record, null);
        ViewHelper.inject(this, view);

        // 初始化参数
        Bundle bundle = getArguments();
        deviceId = bundle.get(KEY_DEVICEID).toString();
        camLive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        textViewCardMsg.setVisibility(View.GONE);

        // 初始化标题
        viewController.setTitle(R.string.local_record);

        // 初始化按钮禁用状态
        setEnable(false);

        // 设置屏幕相关参数，不锁屏，自动横竖屏切换
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // 设置cover图
        if (camLive.getThumbnail() != null && camLive.getThumbnail().length() > 0) {
            Picasso.with(getActivity()).load(camLive.getThumbnail()).into(target);
        }

        // 初始化事件监听
        videoView.setOnCompletionListener(this);
        videoView.setOnVideoSizeChangedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnBufferingUpdateListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnPreparedListener(this);
        videoView.setOnBufferingUpdateListener(this);
        viewTop.setOnTouchListener(this);
        viewController.setOnControlListener(this);
        viewController.setViewTop(viewTop);
        viewController.setVideoView(videoView);

        // 设置视频播放高度，广角移动范围
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;
        int videoHeight = 0;
        if (screenHeight > screenWidth) {
            videoHeight = screenWidth * 3 / 4;
        } else {
            videoHeight = screenHeight * 3 / 4;
        }
        viewController.setVideoHeight(videoHeight);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewTop.getLayoutParams();
        params.height = videoHeight;
        int videoWidth = videoHeight * 16 / 9;
        if (screenHeight > screenWidth) {
            viewController.setLeftAndRightMergin((videoWidth - screenWidth) / 2);
        } else {
            viewController.setLeftAndRightMergin((videoWidth - screenHeight) / 2);
        }

        // 初始化播放监听
        ErmuBusiness.getStreamMediaBusiness().registerListener(OnOpenPlayCardRecordListener.class, this);

        // 初始化声音状态
        preBusiness = ErmuBusiness.getPreferenceBusiness();
        boolean isSoundOpen = preBusiness.getSoundStatus(deviceId, PreferenceConfig.SoundType.record);
        if (isSoundOpen != viewController.isVoiceOn()) {
            viewController.changeVoiceState();
            onSoundStateChange();
        }

        // 获取录像数据
        business = ErmuBusiness.getMineRecordBusiness();
        business.registerListener(OnNasRecordChangedListener.class, this);
        business.findNasRecordList(deviceId + "nas", DateUtil.stringtoDate("1984-01-01 00:00:00", DateUtil.FORMAT_ONE).getTime(), new Date().getTime());

        // 初始化缩略图列表适配器
        timeLineView.setOnTouchListener(this);

        // 初始化时间轴
        String dateStr = DateUtil.formatDate(new Date(), DateUtil.FORMAT_ONE);
        viewController.setCurrentTime(dateStr, false);
        timeLineView.setListener(this);

        // 初始化日期面板适配器
        List<CamDate> camDates = business.getDayTimeList(deviceId + "nas");
        gridAdapter = new RecordDateGridAdapter(getActivity());
        gridAdapter.setAdapterType(RecordDateGridAdapter.TYPE_FRAGMENT);
        gridAdapter.setDates(camDates);
        gridViewDates.setAdapter(gridAdapter);
        gridAdapterCtrl = new RecordDateGridAdapter(getActivity());
        gridAdapterCtrl.setAdapterType(RecordDateGridAdapter.TYPE_CONTROLLER);
        gridAdapterCtrl.setDates(camDates);
        viewController.setGridViewDateAdapter(gridAdapterCtrl);
        gridViewDates.setOnItemClickListener(this);

        // 初始化播放状态
        viewStatus = viewController.getViewStatus();
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.in_loading_please), null);
        viewStatus.setListener(this);
        refreshStatusView();

        // 播放缓存录像
        onRecordChanged(null);

        return view;
    }

    /**
     * ********************************系统回调方法****************************************************
     */

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (bitmapCover != null && !bitmapCover.isRecycled()) {
            videoView.setVideoCover(bitmapCover);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 把横向listView从viewController里转移到fragment里
            viewController.removeHorizontalView(viewTimeLineParent);
            viewHorizontal.addView(viewTimeLineParent);
            // 处理竖屏控件的布局
            viewController.changeToPortrait();
            // 重新刻画时间轴
            drawTimeLine();
            timeLineView.scrollTo(currentPlayingTime);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            showActionBar();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 关闭打开的弹出框
            if (dialogCardInfo != null) {
                dialogCardInfo.dismiss();
            }
            // 隐藏日期选择面板
            viewDateSelectBg.setVisibility(View.GONE);
            viewTimeList.setVisibility(View.GONE);
            // 把横向listView从fragment里转移到viewController里
            viewHorizontal.removeView(viewTimeLineParent);
            viewController.setHorizontalView(viewTimeLineParent);
            // 处理横屏控件的布局
            viewController.changeToLandscape();
            // 重新刻画时间轴
            drawTimeLine();
            timeLineView.scrollTo(currentPlayingTime);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideActionBar();
        }
    }

    @OnClick(value = {R.id.buttonPlay, R.id.buttonCamera, R.id.buttonSound, R.id.buttonFullScreen, R.id.live_mine_close,
            R.id.viewTime, R.id.textViewCloseTimeList, R.id.buttonNotice, R.id.textViewSelectDate,
            R.id.buttonIKnow})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPlay:
                changePlayState();
                break;
            case R.id.buttonSound:
                viewController.changeVoiceState();
                onSoundStateChange();
                break;
            case R.id.buttonFullScreen:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case R.id.live_mine_close:
                popBackStack();
                break;
            case R.id.textViewCloseTimeList:
                animateHideDateSelect();
                break;
            case R.id.buttonCamera:
                viewController.printScreen();
                printScreenTime(100);
                break;
            case R.id.textViewSelectDate:
                animateShowDateSelect();
                break;
            case R.id.buttonIKnow:
                if (listener != null) {
                    listener.onIKnowClick();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.viewTop) {
            viewController.onTopTouch(v, event);
        } else {
            if (v.getId() == R.id.timeLineView) {
                viewController.onScrollViewTouch(v, event);
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.gridViewDates) {
            CamDate camDate = gridAdapter.getItem(position);
            if (camDate.isExistRecord()) {
                animateHideDateSelect();
                setDayText(position);
                changeDay(position);
            }
        }
    }

    public void onFragmentResume() {
        Logger.i("onFragmentResume");
        if (bitmapCover != null && !bitmapCover.isRecycled()) {
            videoView.setVideoCover(bitmapCover);
        }

        if (!viewStatus.isViewStatusShow()) {  //第二次加载页面
            if (getActivity() != null && !Util.isNetworkAvailable(getActivity())) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.please_check_network), null);
            } else {
                Logger.i("IErmuSDK.java", ">>>>>>> onResume");
                playRecord(currentPlayingTime);
            }
        }
    }

    public void onFragmentPause() {
        Logger.i("onFragmentPause");
        ErmuBusiness.getStreamMediaBusiness().closeRecord(deviceId);
        videoView.stopPlayback(false);
    }

    public void onFragmentDestroy() {
        ErmuBusiness.getStreamMediaBusiness().closeRecord(deviceId);

        if (videoView != null) videoView.stopPlayback(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.i("IErmuSDK.java", ">>>>>>> onDestroyView");
        Picasso.with(getActivity()).cancelRequest(target);
        business.unRegisterListener(OnNasRecordChangedListener.class, this);
        ErmuBusiness.getStreamMediaBusiness().unRegisterListener(OnOpenPlayCardRecordListener.class, this);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        if (videoView != null) videoView.stopPlayback(true);
        ErmuBusiness.getStreamMediaBusiness().closeRecord(deviceId);
        if (currentTimeTimer != null) {
            currentTimeTimer.cancel();
        }

        // 清除屏幕不锁定参数
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 停止后台数据加载
        business.stopLoadData(deviceId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewController.isLandscape()) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ********************************控制器回调方法****************************************************
     */

    @Override
    public void onSoundClick() {
        onSoundStateChange();
    }

    @Override
    public void onOutFullScreenClick() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    @Override
    public void onPlayClick() {
        changePlayState();
    }


    @Override
    public void onDateItemClick(int position) {
        CamDate camDate = gridAdapterCtrl.getItem(position);
        if (camDate.isExistRecord()) {
            viewController.hideViewGridViewDate();
            setDayText(position);
            changeDay(position);
        }
    }

    @Override
    public void onPrintScreenClick() {
        viewController.printScreen();
        printScreenTime(100);
    }

    @Override
    public void onCutImageHide() {
        imageButtonPrintScreen.setEnabled(true);
        viewController.setButtonPrintScreenEnabled(true);
    }

    @Override
    public void onViewCutClick() {
        Fragment fragment = ShareImageFragment.actionInstance(camLive.getDescription(), imageTime);
        addToBackStack(fragment);
    }

    @Override
    public void onPrintScreenComplete(final Bitmap bitmap, long imageTime) {
        this.imageTime = imageTime;
        if (bitmap == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (viewController != null) viewController.setImage(bitmap);
            }
        });
    }

    @Override
    public void onSaveScreenImage(final Bitmap bitmap, final long imageTime) {
        ShareUtil.saveBitmap(bitmap, imageTime, deviceId);
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

    /**
     * ********************************业务数据改变回调方法****************************************************
     */

    @Override
    public void onRecordChanged(ErrorCode errorCode) {
        findCardRecordNum++;
        Logger.i("IErmuSDK.java", ">>>>>>> onRecordChanged");
        Logger.i("findCardRecordNum:" + findCardRecordNum);
        if (errorCode == null || errorCode.getIndex() == ErrorCode.SUCCESS.ordinal() || findCardRecordNum >= 3) {
            // 刷新日期面板，设置每一天有没有录像
            gridAdapter.setDates(business.getDayTimeList(deviceId + "nas"));
            gridAdapter.notifyDataSetChanged();
            gridAdapterCtrl.setDates(business.getDayTimeList(deviceId + "nas"));
            gridAdapterCtrl.notifyDataSetChanged();

            List<CamRecord> deviceRecrods = business.getRecordList(deviceId + "nas");
            if (deviceRecrods.size() == 0 && errorCode != null) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_CAM_OFF, null, getString(R.string.no_video), null);
                timeLineView.scrollToEnd();
                return;
            }
            drawTimeLine();
            if (!isPlayed && deviceRecrods.size() > 0 || errorCode == null && deviceRecrods.size() > 0) {
                List<CamDate> camDates = business.getDayTimeList(deviceId + "nas");
                CamRecord camRecord = deviceRecrods.get(deviceRecrods.size() - 1);
                currentPlayingTime = camRecord.getStartTime();
                String dateStr = DateUtil.formatDate(new Date(currentPlayingTime * 1000l), DateUtil.FORMAT_ONE);
                viewController.setCurrentTime(dateStr, true);
                Logger.d("onRecordChangedCurrentTime:" + DateUtil.formatDate(new Date(currentPlayingTime * 1000l), DateUtil.FORMAT_ONE));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timeLineView.scrollTo(currentPlayingTime);
                    }
                }, 300);

                for (int i = 0; i < camDates.size(); i++) {
                    CamDate camDate = camDates.get(i);
                    if (currentPlayingTime > camDate.getDayStartTime()) {
                        setDayText(i);
                        break;
                    }
                }

                playRecord(currentPlayingTime);
                isPlayed = true;
            }
        } else {
            business.findNasRecordList(deviceId + "nas", DateUtil.stringtoDate("1984-01-01 00:00:00", DateUtil.FORMAT_ONE).getTime(), new Date().getTime());
        }
    }

    /**
     * ********************************状态转换器回调方法****************************************************
     */

    @Override
    public void onRefreshClick(PublicCamStatusView view) {
        Logger.i("IErmuSDK.java", ">>>>>>> onRefreshClick");
        playRecord(currentPlayingTime);
    }

    @Override
    public void onButtonResolveClick(PublicCamStatusView view) {
        WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_SOLVE);
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

    /**
     * ********************************播放器回调方法****************************************************
     */

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        Logger.i("onCompletion " + iMediaPlayer.getCurrentPosition());
        if (currentTimeTimer != null) {
            currentTimeTimer.cancel();
        }
        viewController.setIsPlaying(false);
        List<CamRecord> records = business.getRecordList(deviceId + "nas");
        if (positionPlaying >= records.size() - 1) {
            viewController.changePlayStateButton();
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_CAM_OFF, null, getString(R.string.dragging_time_line), null);
            setEnable(false);
            return;
        }

        if (nextStartTime == 0) {
            CamRecord camRecord = records.get(positionPlaying + 1);
            currentPlayingTime = camRecord.getStartTime();
            timeLineView.scrollTo(currentPlayingTime);
        } else {
            currentPlayingTime = nextStartTime;
        }

        List<CamDate> camDates = business.getDayTimeList(deviceId + "nas");
        for (int i = 0; i < camDates.size(); i++) {
            CamDate camDate = camDates.get(i);
            if (currentPlayingTime > camDate.getDayStartTime()) {
                setDayText(i);
                break;
            }
        }

        playRecord(currentPlayingTime);
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int i, int i1, int i2, int i3) {

    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i2) {
        Logger.i("onError", "errorcode:" + i);
        setEnable(false);
        videoView.stopPlayback(false);
        if (bitmapCover != null && !bitmapCover.isRecycled()) {
            videoView.setVideoCover(bitmapCover);
        }
        if (currentTimeTimer != null) {
            currentTimeTimer.cancel();
        }
        if (i == -10000) {
            if (getActivity() != null && !Util.isNetworkConn(getActivity())) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.network_error_please_check), null);
            } else {
                if (autoRetryNum >= 2) {
                    autoRetryNum = 0;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.load_error_repeat), null);
                } else {
                    autoRetryNum++;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.in_loading_please), null);

                    List<CamRecord> list = business.getRecordList(deviceId + "nas");
                    CamRecord camRecord = list.get(positionPlaying);
                    if (Math.abs(currentPlayingTime - camRecord.getEndTime()) < 5) {
                        positionPlaying++;
                        if (positionPlaying >= list.size()) {
                            viewController.changePlayStateButton();
                            viewStatus.showViewStatus(PublicCamStatusView.STATUS_CAM_OFF, null, getString(R.string.dragging_time_line), null);
                            setEnable(false);
                        } else {
                            camRecord = list.get(positionPlaying);
                            playRecord(camRecord.getStartTime());
                        }
                    } else {
                        playRecord(currentPlayingTime);
                    }

                    Logger.i("IErmuSDK.java", ">>>>>>> onError");
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
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        Logger.i("onBufferingUpdate " + i);
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i2) {
        Logger.i("onInfo i=" + i + " i2=" + i2);
        switch (i) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START://第一帧上屏显示
                setEnable(true);
                break;
            case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Logger.d("MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                Logger.d("MEDIA_INFO_METADATA_UPDATE");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                Logger.d("MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Logger.d("MEDIA_INFO_NOT_SEEKABLE");
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        if (!viewController.isVoiceOn()) {
            videoView.mute(1);// 声音关
        }

        viewStatus.hideViewStatus();
        autoRetryNum = 0;

        viewController.setIsPlaying(true);
        changePlayStateButton();
        viewController.changePlayStateButton();

        if (currentTimeTimer != null) {
            currentTimeTimer.cancel();
        }
        currentTimeTimer = new Timer();
        currentTimeTimer.schedule(new CurrentTimeTask(), 0, 500);
    }

    @Override
    public void onOpenPlayCardRecord(String playUrl, ErrorCode errorCode) {
        if (errorCode.getIndex() != ErrorCode.SUCCESS.ordinal()) {
            playRecord(currentPlayingTime);
            return;
        }

        Logger.i("cardPlayUrl:" + playUrl);
        videoView.bufferON(true);
        videoView.setDelayMS(0);
        videoView.playVideo(playUrl, false);
    }

    /**
     * ********************************时间轴回调方法****************************************************
     */

    @Override
    public void onScroll(long scrollToTime) {
        isOnScroll = true;
        Logger.d("scrollX:" + DateUtil.formatDate(new Date(scrollToTime), DateUtil.FORMAT_ONE));
        currentPlayingTime = (int) (scrollToTime / 1000);
        String dateStr = DateUtil.formatDate(new Date(scrollToTime), DateUtil.FORMAT_ONE);
        viewController.setCurrentTime(dateStr, true);

    }

    @Override
    public void onTouchUp() {
        if (currentTimeTimer != null) {
            currentTimeTimer.cancel();
        }
        isOnScroll = false;
        List<CamDate> camDates = business.getDayTimeList(deviceId + "nas");
        if (camDates != null) {
            for (int i = 0; i < camDates.size(); i++) {
                CamDate camDate = camDates.get(i);
                if (currentPlayingTime > camDate.getDayStartTime()) {
                    Logger.d("dayStartTime:" + DateUtil.formatDate(new Date(camDate.getDayStartTime() * 1000l), DateUtil.FORMAT_ONE));
                    setDayText(i);
                    break;
                }
            }
        }

        playRecord(currentPlayingTime);
    }

    @Override
    public void onTimeLineTouch(View v, MotionEvent event) {
        viewController.onScrollViewTouch(v, event);
    }

    /**
     *********************************私有方法****************************************************
     */

    /**
     * 从指定时间开始播放录像
     */
    private synchronized void playRecord(int startTime) {
        if (currentTimeTimer != null) {
            currentTimeTimer.cancel();
        }
        // 根据开始时间遍历查找对应录像
        CamRecord record = null;
        List<CamRecord> records = business.getRecordList(deviceId + "nas");
        for (int i = 0; i < records.size(); i++) {
            CamRecord camRecord = records.get(i);
            CamRecord camRecordNext = (i < records.size() - 1) ? records.get(i + 1) : null;

            if (startTime < camRecord.getStartTime() && (camRecord.getStartTime() - startTime) < 150) {
                // 如果开始时间小于录像开始时间
                // 并且两者相差小于2.5分钟，则跳到该录像开始时间
                startTime = camRecord.getStartTime();
                record = camRecord;
                positionPlaying = i;
                break;
            } else if (startTime >= camRecord.getStartTime() && startTime < camRecord.getEndTime()) {
                // 如果开始时间在某段录像中间，则播放该录像
                record = camRecord;
                positionPlaying = i;
                break;
            } else if (startTime > camRecord.getEndTime()
                    && (startTime - camRecord.getEndTime() < 150)
                    && (camRecordNext != null && startTime < camRecordNext.getStartTime())
                    && (camRecordNext.getStartTime() - startTime > 150)
                    && (camRecord.getEndTime() - camRecord.getStartTime()) < 120) {
                // 如果开始时间大于当前录像的结束时间,大于下一段的开始时间
                // 并且开始时间和下一段录像的开始时间相差大于2.5分钟
                // 并且当前录像的长度小于2分钟（不好点），则跳到当前录像开始时间播放
                startTime = camRecord.getStartTime();
                record = camRecord;
                positionPlaying = i + 1;
                break;
            }
        }

        currentPlayingStartTime = startTime;
        videoView.stopPlayback(false);
        if (record == null) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_CAM_OFF, null, getString(R.string.the_time_no_video), null);
            setEnable(false);
            return;
        }

        int endTime = record.getEndTime();
        if (endTime - startTime > 900) {
            nextStartTime = startTime + 900;
            endTime = startTime + 900;
        } else {
            nextStartTime = 0;
        }

        timeLineView.scrollTo(currentPlayingStartTime);
        ErmuBusiness.getStreamMediaBusiness().openCardPlayRecord(deviceId, startTime, endTime);
        playPosition = 0;
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.wait_load), null);

        setEnable(false);
    }

    /**
     * 设置按钮禁用状态
     *
     * @param enable
     */
    private void setEnable(boolean enable) {
        buttonPlay.setEnabled(enable);
        viewController.setButtonPlayEnable(enable);
        imageButtonPrintScreen.setEnabled(enable);
        viewController.setButtonPrintScreenEnabled(enable);
        viewController.setIsIndicatorShow(enable);
        buttonSound.setEnabled(enable);
        viewController.setButtonVoiceEnable(enable);
    }

    // 声音状态改变时调用
    private void onSoundStateChange() {
        if (viewController.isVoiceOn()) {
            buttonSound.setSelected(false);
            videoView.mute(0);// 声音开
        } else {
            buttonSound.setSelected(true);
            videoView.mute(1);// 声音关
        }
        preBusiness.setSoundStatus(deviceId, PreferenceConfig.SoundType.record, viewController.isVoiceOn());
    }

    /**
     * 切换播放状态
     */
    private void changePlayState() {
        Logger.i("IErmuSDK.java", ">>>>>>> changePlayState");
        if (viewController.isPlaying()) {
            if (videoView != null) videoView.pause();
        } else {
            if (videoView != null) videoView.start();
        }
        viewController.setIsPlaying(!viewController.isPlaying());
        changePlayStateButton();
        viewController.changePlayStateButton();
    }

    // 播放状态改变时调用
    private void changePlayStateButton() {
        if (viewController.isPlaying()) {
            buttonPlay.setSelected(false);
        } else {
            buttonPlay.setSelected(true);
        }
    }

    /**
     * 初始化判断播放状态
     */
    private void refreshStatusView() {
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
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.network_error_please_check), null);
        }
    }

    /**
     * 设置日期选择按钮文字
     *
     * @param position
     */
    private void setDayText(int position) {
        if (position == 0) {
            textViewTime.setText(getString(R.string.today));
            viewController.setDate(getString(R.string.today));
        } else if (position == 1) {
            textViewTime.setText(getString(R.string.yesterday));
            viewController.setDate(getString(R.string.yesterday));
        } else {
            Date date = new Date(gridAdapter.getItem(position).getDayStartTime() * 1000l);
            String dateStr = DateUtil.formatDate(date, DateUtil.SHORT_DATE_FORMAT3);
            textViewTime.setText(dateStr);
            viewController.setDate(dateStr);
        }

        gridAdapter.setSelectPosition(position);
        gridAdapterCtrl.setSelectPosition(position);
        gridAdapter.notifyDataSetChanged();
        gridAdapterCtrl.notifyDataSetChanged();
    }

    /**
     * 切换日期操作
     *
     * @param position
     */
    private void changeDay(int position) {
        List<CamDate> camDates = business.getDayTimeList(deviceId + "nas");
        int endTime = camDates.get(position).getDayStartTime() + DateUtil.DAY_SECOND_NUM;
        List<CamRecord> records = business.getRecordList(deviceId + "nas");
        CamRecord camRecord = null;
        int selectIndex = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getEndTime() < endTime) {
                camRecord = records.get(i);
                selectIndex = i;
            }
        }

        // 处理所选录像时长小于一秒，开始时间等于结束时间的问题
        if (camRecord.getStartTime() == camRecord.getEndTime() && selectIndex > 0) {
            camRecord = records.get(selectIndex - 1);
        }
        if (camRecord != null) {
            currentPlayingTime = camRecord.getStartTime();
            timeLineView.scrollTo(currentPlayingTime);
            playRecord(currentPlayingTime);
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

    /**
     * 刻画时间轴
     */
    private void drawTimeLine() {
        int windowWidth;
        boolean isLanscape = viewController.isLandscape();
        if (isLanscape) {
            windowWidth = screenWidth > screenHeight ? screenWidth : screenHeight;
        } else {
            windowWidth = screenWidth > screenHeight ? screenHeight : screenWidth;
        }
        // 初始化时间轴
        long endTime = new Date().getTime() / 1000;
        long startTime = 0;
        List<CamRecord> records = business.getRecordList(deviceId + "nas");
        if (records != null && records.size() > 0) {
            startTime = records.get(0).getStartTime();
            int startTimehour = (int) (startTime / (ExpandTimeLineView.SECOND_PER_PIX * 300));
            startTime = startTimehour * (ExpandTimeLineView.SECOND_PER_PIX * 300);
        }
        timeLineView.draw(startTime, endTime, windowWidth, records);
    }

    /**
     * 获取当前播放时间
     */
    private class CurrentTimeTask extends TimerTask {
        @Override
        public void run() {
            if (!isOnScroll) {
                Logger.d("currentPosition:" + videoView.getCurrentPosition());
                int playPositionNow = videoView.getCurrentPosition();
                if (playPositionNow > playPosition) {
                    playPosition = playPositionNow;
                }

                final long currentTime = currentPlayingStartTime * 1000l + playPosition;
                currentPlayingTime = (int) (currentTime / 1000);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeLineView.scrollTo(currentPlayingTime);
                            String dateStr = DateUtil.formatDate(new Date(currentTime), DateUtil.FORMAT_ONE);
                            viewController.setCurrentTime(dateStr, false);
                        }

                    });
                }
            }
        }
    }

    /**
     * 动画显示日期选择面板
     */
    private void animateShowDateSelect() {
        YoYo.with(Techniques.SlideInUp).duration(300).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewTimeList.setVisibility(View.VISIBLE);
                viewDateSelectBg.setVisibility(View.VISIBLE);
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
        }).playOn(viewTimeList);
    }

    /**
     * 动画隐藏日期选择面板
     */
    private void animateHideDateSelect() {
        YoYo.with(Techniques.SlideOutDown).duration(300).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewDateSelectBg.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(viewTimeList);
    }

    /**
     * 公共方法隐藏日期
     *
     * @return
     */
    public boolean hideSelectDate() {
        if (viewDateSelectBg.getVisibility() == View.VISIBLE) {
            animateHideDateSelect();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击我知道了按钮回调
     */
    public interface IKnowCallBack {
        void onIKnowClick();
    }
}
