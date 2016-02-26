package com.iermu.ui.fragment.MineIermu;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.IMineRecordBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.config.PreferenceConfig;
import com.iermu.client.listener.OnClipListener;
import com.iermu.client.listener.OnOpenPlayRecordListener;
import com.iermu.client.listener.OnRecordChangedListener;
import com.iermu.client.listener.OnThumbnailChangedListener;
import com.iermu.client.listener.OnVodSeekListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamDate;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamThumbnail;
import com.iermu.client.model.RecordMedia;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.viewmodel.MimeCamItem;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.Logger;
import com.iermu.lan.model.CamRecord;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.adapter.RecordDateGridAdapter;
import com.iermu.ui.adapter.RecordListAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CloudInformDialog;
import com.iermu.ui.view.CloudStartCloseDialog;
import com.iermu.ui.view.ExpandTimeLineView;
import com.iermu.ui.view.PublicCamStatusView;
import com.iermu.ui.view.RecordLiveControllerView;
import com.iermu.ui.view.filmEdit.FilmEditFailDialog;
import com.iermu.ui.view.filmEdit.FilmEditLittleProgressView;
import com.iermu.ui.view.filmEdit.FilmEditProgressDialog;
import com.iermu.ui.view.filmEdit.FilmEditSelectDateDialog;
import com.iermu.ui.view.filmEdit.FilmEditTipsDialog;
import com.iermu.ui.view.timeline.HorizontalListView;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangxq on 15/7/27.
 */
public class MineRecordFragment extends BaseFragment implements View.OnClickListener,
        RecordLiveControllerView.OnControlListener, AdapterView.OnItemClickListener,
        OnRecordChangedListener, PublicCamStatusView.PublicStatusListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnPreparedListener,
        View.OnTouchListener, OnOpenPlayRecordListener, OnThumbnailChangedListener,
        ExpandTimeLineView.TimeLineScrollListener, OnVodSeekListener, OnClipListener {

    private static final String KEY_DEVICEID = "deviceId";
    private static final String KEY_IS_LANSCAPE = "isLanscape";

    @ViewInject(R.id.viewTop)
    RelativeLayout viewTop;
    @ViewInject(R.id.videoView)
    VideoView videoView;
    @ViewInject(R.id.viewController)
    RecordLiveControllerView viewController;
    @ViewInject(R.id.buttonPlay)
    ImageButton buttonPlay;
    @ViewInject(R.id.buttonCamera)
    ImageButton imageButtonPrintScreen;
    @ViewInject(R.id.buttonSound)
    ImageButton buttonSound;
    @ViewInject(R.id.viewTimeLineParent)
    View viewTimeLineParent;
    @ViewInject(R.id.listViewTime)
    HorizontalListView listViewTime;
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
    @ViewInject(R.id.textViewRecordInfo)
    TextView textViewRecordInfo;
    @ViewInject(R.id.gridViewDates)
    GridView gridViewDates;
    @ViewInject(R.id.textViewTimeLine)
    TextView textViewTimeLine;
    @ViewInject(R.id.textViewThumbnail)
    TextView textViewThumbnail;
    @ViewInject(R.id.viewBottomSwitch)
    View viewBottomSwitch;
    @ViewInject(R.id.viewOutDate)
    View viewOutDate;
    @ViewInject(R.id.viewNoRecord)
    View viewNoRecord;

    // 剪辑相关控件
    @ViewInject(R.id.textViewRecordCut)
    TextView textViewRecordCut;
    @ViewInject(R.id.viewFilmEdit)
    View viewFilmEdit;
    @ViewInject(R.id.viewLittleProgress)
    FilmEditLittleProgressView viewLittleProgress;
    @ViewInject(R.id.viewEditProgressBottom)
    View viewEditProgressBottom;
    @ViewInject(R.id.imageViewEditFildBottom)
    ImageView imageViewEditFildBottom;
    //云录像过期文案
    @ViewInject(R.id.support_days_txt)
    TextView mSupportDaysTxt;
    @ViewInject(R.id.save_clip_txt)
    TextView mSaveClipTxt;
    @ViewInject(R.id.support_online_txt)
    TextView mSupportOnlineTxt;


    private PublicCamStatusView viewStatus;

    // 数据适配器
    private RecordListAdapter adapter;
    private RecordDateGridAdapter gridAdapter;
    private RecordDateGridAdapter gridAdapterCtrl;

    private CamLive camLive;
    private String deviceId;
    private IMineRecordBusiness business;
    private IPreferenceBusiness preBusiness;
    private IMimeCamBusiness camBusiness;
    private long imageTime; // 截图时间

    // 剪辑相关参数
    private FilmEditTipsDialog filmDialog; // 提示剪辑保存弹框
    private FilmEditSelectDateDialog selectDateDialog; // 选择时间弹框
    private FilmEditProgressDialog progressDialog; // 保存进度弹框
    private FilmEditFailDialog failDialog; // 保存失败弹框
    private boolean isFilmEditShowd; // 记录剪辑提示是否显示过
    private boolean isShowClip = true; // 记录是否应该显示剪辑按钮
    private boolean isCliping = false; // 记录是否正在剪辑
    private boolean isStopByClip; // 记录是否因为剪辑而暂停了播放
    private boolean isFilmEditFaild; // 记录上次剪辑是否失败

    // 播放相关参数
    private int currentPlayingTime; // 记录当前播放的时间秒
    private int currentPlayingStartTime; // 记录开始播放时间
    private int positionPlaying; // 记录当前录像的位置
    private Timer currentTimeTimer; // 循环获取当前播放的时间
    private int currentThumbnailPosition; // 记录当前选中缩略图位置
    private boolean isOnScroll; // 记录是否正在滚动时间轴
    private int screenWidth; // 屏幕宽度
    private int screenHeight; // 屏幕高度
    private int seekNum; // 记录是第几次校正，保证数据同步
    private boolean isPlayed = false; // 记录是否初始化的时候播放了
    private int nextStartTime; // 记录录像长度大于15分钟时的下一次播放开始时间

    long yy;

    // 状态转换有关参数
    private int autoRetryNum;

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
     * @param isLanscape
     * @return
     */
    public static Fragment actionInstance(String deviceId, boolean isLanscape) {
        MineRecordFragment fragment = new MineRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEVICEID, deviceId);
        bundle.putBoolean(KEY_IS_LANSCAPE, isLanscape);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 打开录像页面
     *
     * @param deviceId
     * @return
     */
    public static Fragment actionInstance(String deviceId) {
        return actionInstance(deviceId, false);
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {

    }

    /**
     * 外部调用toast
     *
     * @param msg
     */
    public void toast(String msg) {
        if (Integer.parseInt(camLive.getCvrDay()) > 0) {
            viewController.toast(msg);
        } else {
            ErmuApplication.toast(msg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_mine_record, null);
        ViewHelper.inject(this, view);

        // 初始化参数
        Bundle bundle = getArguments();
        deviceId = bundle.get(KEY_DEVICEID).toString();
        Boolean isLanscape = bundle.getBoolean(KEY_IS_LANSCAPE);
        camLive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);

        // 设置cover图
        if (camLive.getThumbnail() != null && camLive.getThumbnail().length() > 0) {
            Picasso.with(getActivity()).load(camLive.getThumbnail()).into(target);
        }

        // 初始化标题
        viewController.setTitle(R.string.cloud_record);

        // 初始化按钮禁用状态
        setEnable(false);

        // 设置屏幕相关参数，不锁屏，自动横竖屏切换
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (isLanscape) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

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

        // 初始化云录制状态
        showCircle(mSupportDaysTxt, getString(R.string.card_support_days));
        showCircle(mSaveClipTxt, getString(R.string.card_clip));
        showCircle(mSupportOnlineTxt, getString(R.string.card_support_platform));
        if (camLive.getConnectType() == ConnectType.LINYANG) {
            camLive.setCvrDay("7");
            viewFilmEdit.setVisibility(View.GONE);
            isShowClip = false;
        } else {
            if (Integer.parseInt(camLive.getCvrDay()) > 0) {
                textViewRecordInfo.setVisibility(View.VISIBLE);
            } else {
                viewOutDate.setVisibility(View.VISIBLE);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        // 初始化播放监听
        ErmuBusiness.getStreamMediaBusiness().registerListener(OnOpenPlayRecordListener.class, this);
        ErmuBusiness.getStreamMediaBusiness().registerListener(OnVodSeekListener.class, this);

        // 初始化声音状态
        preBusiness = ErmuBusiness.getPreferenceBusiness();
        boolean isSoundOpen = preBusiness.getSoundStatus(deviceId, PreferenceConfig.SoundType.record);
        if (isSoundOpen != viewController.isVoiceOn()) {
            viewController.changeVoiceState();
            onSoundStateChange();
        }

        // 初始化剪辑
        camBusiness = ErmuBusiness.getMimeCamBusiness();
        List<MimeCamItem> camItemList = camBusiness.getCamItemList();
        for (MimeCamItem item : camItemList) {
            if (item.getItemType() == MimeCamItem.TYPE_COLLECTITEM || item.getItemType() == MimeCamItem.TYPE_COLLECT) {
                continue;
            }
            if (item.getItem().getDeviceId().equals(deviceId) && item.getItemType() == MimeCamItem.TYPE_AUTHORIZE) {
                viewFilmEdit.setVisibility(View.GONE);
                isShowClip = false;
            }
        }
        isFilmEditShowd = preBusiness.getFilmEditIsShowd();
        isFilmEditFaild = preBusiness.getFilmEditIsFaild();
        if (isFilmEditFaild) {
            imageViewEditFildBottom.setVisibility(View.VISIBLE);
            viewEditProgressBottom.setVisibility(View.GONE);
            textViewRecordCut.setText(getString(R.string.clip_fail));
        } else {
            imageViewEditFildBottom.setVisibility(View.GONE);
            viewEditProgressBottom.setVisibility(View.VISIBLE);
            textViewRecordCut.setText(getString(R.string.film_edit));
        }

        // 获取录像和缩略图数据
        business = ErmuBusiness.getMineRecordBusiness();
        business.registerListener(OnRecordChangedListener.class, this);
        business.registerListener(OnThumbnailChangedListener.class, this);
        business.initData(deviceId, Integer.parseInt(camLive.getCvrDay()));
        business.findRecordList(deviceId);
        business.findThumbnailList(deviceId, Integer.parseInt(camLive.getCvrDay()));
        business.findCardInfo(deviceId);

        //初始化剪辑
        business.registerListener(OnClipListener.class, this);
        selectDateDialog = new FilmEditSelectDateDialog(getActivity());
        selectDateDialog.init(business.getDayTimeList(deviceId));
        viewLittleProgress.initDate(23);
        viewLittleProgress.invalidate();
        if (business.getIsCliping()) {
            isCliping = true;
            viewLittleProgress.setVisibility(View.VISIBLE);
            progressDialog = new FilmEditProgressDialog(getActivity());
        }

        // 初始化缩略图列表适配器
        adapter = new RecordListAdapter(getActivity());
        listViewTime.setAdapter(adapter);
        listViewTime.setOnItemClickListener(this);
        listViewTime.setOnTouchListener(this);
        timeLineView.setOnTouchListener(this);

        // 初始化时间轴
        String dateStr = DateUtil.formatDate(new Date(), DateUtil.FORMAT_ONE);
        viewController.setCurrentTime(dateStr, false);
        timeLineView.setListener(this);

        // 初始化日期面板适配器
        List<CamDate> camDates = business.getDayTimeList(deviceId);
        gridAdapter = new RecordDateGridAdapter(getActivity());
        gridAdapter.setAdapterType(RecordDateGridAdapter.TYPE_FRAGMENT);
        gridAdapter.setDates(camDates);
        gridViewDates.setAdapter(gridAdapter);
        gridAdapterCtrl = new RecordDateGridAdapter(getActivity());
        gridAdapterCtrl.setAdapterType(RecordDateGridAdapter.TYPE_CONTROLLER);
        gridAdapterCtrl.setDates(camDates);
        viewController.setGridViewDateAdapter(gridAdapterCtrl);
        gridViewDates.setOnItemClickListener(this);

        // 初始化时间轴和缩略图切换按钮位置
        Animation animation1 = new TranslateAnimation(0, Util.DensityUtil.dip2px(getActivity(), 30), 0, 0);
        animation1.setFillAfter(true);
        viewBottomSwitch.startAnimation(animation1);

        // 初始化播放状态
        viewStatus = viewController.getViewStatus();
        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.in_loading_please), null);
        viewStatus.setListener(this);
        refreshStatusView();

        // 播放缓存录像
        onRecordChanged(null);
        onThumbnailChange();

        return view;
    }

    /**
     * ********************************系统回调方法****************************************************
     */

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 把横向listView从viewController里转移到fragment里
            viewController.removeHorizontalView(viewTimeLineParent);
            viewHorizontal.addView(viewTimeLineParent);
            // 刷新缩略图列表
            adapter.setAdapterType(RecordListAdapter.TYPE_FRAGMENT);
            adapter.notifyDataSetChanged();
            listViewTime.setSelection(currentThumbnailPosition);
            // 处理竖屏控件的布局
            viewController.changeToPortrait();
            // 重新刻画时间轴
            drawTimeLine(viewController.isLandscape());
            timeLineView.scrollTo(currentPlayingTime);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            showActionBar();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 隐藏弹出框
            if (filmDialog != null) {
                filmDialog.dismiss();
            }
            if (selectDateDialog != null) {
                selectDateDialog.dismiss();
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (failDialog != null) {
                failDialog.dismiss();
            }

            // 隐藏日期选择面板
            viewDateSelectBg.setVisibility(View.GONE);
            viewTimeList.setVisibility(View.GONE);
            // 把横向listView从fragment里转移到viewController里
            viewHorizontal.removeView(viewTimeLineParent);
            viewController.setHorizontalView(viewTimeLineParent);
            // 刷新缩略图列表
            adapter.setAdapterType(RecordListAdapter.TYPE_CONTROLLER);
            adapter.notifyDataSetChanged();
            listViewTime.setSelection(currentThumbnailPosition);
            // 处理横屏控件的布局
            viewController.changeToLandscape();
            // 重新刻画时间轴
            drawTimeLine(viewController.isLandscape());
            timeLineView.scrollTo(currentPlayingTime);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideActionBar();
        }
    }

    @OnClick(value = {R.id.buttonPlay, R.id.buttonCamera, R.id.buttonSound, R.id.buttonFullScreen, R.id.live_mine_close,
            R.id.textViewSelectDate, R.id.textViewCloseTimeList, R.id.textViewRecordInfo, R.id.textViewTimeLine,
            R.id.textViewThumbnail, R.id.buttonDetail, R.id.viewFilmEdit, R.id.buttonNoRecordIKnow})
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
            case R.id.textViewSelectDate:
                animateShowDateSelect();
                break;
            case R.id.textViewCloseTimeList:
                animateHideDateSelect();
                break;
            case R.id.textViewRecordInfo:
                Fragment fragment = RecordInfoFragment.actionInstance(deviceId);
                addToBackStack(fragment);
                break;
            case R.id.buttonCamera:
                viewController.printScreen();
                printScreenTime(100);
                break;
            case R.id.textViewTimeLine:
                switchToTimeLine();
                viewController.switchToTimeLine();
                break;
            case R.id.textViewThumbnail:
                switchToThumbnail();
                viewController.switchToThumbnail();
                break;
            case R.id.buttonDetail:
                WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_CVRBUY);
                break;
            case R.id.viewFilmEdit:
                if (!isFilmEditShowd) {
                    filmDialog = new FilmEditTipsDialog(getActivity());
                    filmDialog.setButtonIKnowListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            filmDialog.dismiss();
                            isFilmEditShowd = true;
                            preBusiness.setFilmEditIsShowd(true);
                            showDateSelectDialog();
                        }
                    });
                    filmDialog.show();
                } else {
                    if (isFilmEditFaild) {
                        imageViewEditFildBottom.setVisibility(View.GONE);
                        viewEditProgressBottom.setVisibility(View.VISIBLE);
                        textViewRecordCut.setText(getString(R.string.film_edit));
                        showFilmEditFaildDialog();
                    } else {
                        showDateSelectDialog();
                    }
                }
                break;
            case R.id.buttonNoRecordIKnow:
                popBackAllStack();
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
            if (v.getId() == R.id.listViewTime || v.getId() == R.id.timeLineView) {
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
        } else if (parent.getId() == R.id.listViewTime) {
            selectThumbnail(position);
            currentThumbnailPosition = position;
        }
    }

    public void onFragmentPause() {
        Logger.i("onFragmentPause");
        ErmuBusiness.getStreamMediaBusiness().unRegisterListener(OnRecordChangedListener.class, this);
        ErmuBusiness.getStreamMediaBusiness().closeRecord(deviceId);
        videoView.stopPlayback(false);
    }

    public void onFragmentResume() {
        Logger.i("onFragmentResume");
        if (bitmapCover != null && !bitmapCover.isRecycled()) {
            videoView.setVideoCover(bitmapCover);
        }
        if (!viewStatus.isViewStatusShow()) {  //第二次加载页面
            if (getActivity() != null && !Util.isNetworkAvailable(getActivity())) {
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.play_no_network), null);
            } else {
                playRecord(currentPlayingTime);
            }
        }
    }

    public void onFragmentDestroy() {
        ErmuBusiness.getStreamMediaBusiness().closeRecord(deviceId);
        if (videoView != null) videoView.stopPlayback(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.i("IErmuSDK.java", "MineRecord >>>>>>> onDestroyView");
        Picasso.with(getActivity()).cancelRequest(target);
        business.unRegisterListener(OnRecordChangedListener.class, this);
        business.unRegisterListener(OnThumbnailChangedListener.class, this);
        business.unRegisterListener(OnClipListener.class, this);
        ErmuBusiness.getStreamMediaBusiness().unRegisterListener(OnOpenPlayRecordListener.class, this);
        ErmuBusiness.getStreamMediaBusiness().unRegisterListener(OnVodSeekListener.class, this);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        ErmuBusiness.getStreamMediaBusiness().closeRecord(deviceId);
        if (videoView != null) videoView.stopPlayback(true);

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
    public void onSoundClick(RecordLiveControllerView view) {
        onSoundStateChange();
    }

    @Override
    public void onOutFullScreenClick(RecordLiveControllerView view) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    @Override
    public void onDeleteClick(RecordLiveControllerView view) {

    }

    @Override
    public void onPlayClick(RecordLiveControllerView view) {
        changePlayState();
    }


    @Override
    public void onDateItemClick(int position, RecordLiveControllerView view) {
        CamDate camDate = gridAdapterCtrl.getItem(position);
        if (camDate.isExistRecord()) {
            viewController.hideViewGridViewDate();
            setDayText(position);
            changeDay(position);
        }
    }

    @Override
    public void onGotoLiveClick(RecordLiveControllerView view) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Fragment fragment = MineLiveFragment.actionInstance(deviceId, true);
        addJumpToPopAllStack(fragment);
    }

    @Override
    public void onPrintScreenClick(RecordLiveControllerView view) {
        viewController.printScreen();
        printScreenTime(100);
    }

    @Override
    public void onCutImageHide(RecordLiveControllerView view) {
        imageButtonPrintScreen.setEnabled(true);
        viewController.setButtonPrintScreenEnabled(true);
    }

    @Override
    public void onViewCutClick(RecordLiveControllerView view) {
        Fragment fragment = ShareImageFragment.actionInstance(camLive.getDescription(), imageTime);
        addToBackStack(fragment);
    }

    @Override
    public void onPrintScreenComplete(RecordLiveControllerView view, final Bitmap bitmap, long imageTime) {
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
    public void onSaveScreenImageComplete(RecordLiveControllerView view, final Bitmap bitmap, final long imageTime) {
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

    @Override
    public void onSwitchToTimeLine() {
        switchToTimeLine();
    }

    @Override
    public void onSwitchToThumbnail() {
        switchToThumbnail();
    }

    /**
     * ********************************业务数据改变回调方法****************************************************
     */

    @Override
    public void onRecordChanged(Business bus) {
        Logger.i("IErmuSDK.java", ">>>>>>> onRecordChanged");
        // 刷新日期面板，设置每一天有没有录像
        gridAdapter.setDates(business.getDayTimeList(deviceId));
        gridAdapter.notifyDataSetChanged();
        gridAdapterCtrl.setDates(business.getDayTimeList(deviceId));
        gridAdapterCtrl.notifyDataSetChanged();

        List<CamRecord> deviceRecrods = business.getRecordList(deviceId);
        if (deviceRecrods.size() == 0 && bus != null) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_CAM_OFF, null, getString(R.string.no_video), null);
            timeLineView.scrollToEnd();
            viewFilmEdit.setVisibility(View.GONE);
            isShowClip = false;
//            if (viewOutDate.getVisibility() != View.VISIBLE) {
//                viewNoRecord.setVisibility(View.VISIBLE);
//            }
            return;
        }

        drawTimeLine(viewController.isLandscape());
        if (!isPlayed && deviceRecrods.size() > 0 || bus == null && deviceRecrods.size() > 0) {
            if (camLive.getConnectType() == ConnectType.BAIDU && isShowClip) {
                viewFilmEdit.setVisibility(View.VISIBLE);
            }
            CamRecord camRecord = deviceRecrods.get(deviceRecrods.size() - 1);
            int endTime = camRecord.getEndTime();
            int startTime = camRecord.getStartTime();
            if (endTime - startTime > 900) {
                startTime = endTime - 900;
            }
            currentPlayingTime = startTime;
            String dateStr = DateUtil.formatDate(new Date(currentPlayingTime * 1000l), DateUtil.FORMAT_ONE);
            viewController.setCurrentTime(dateStr, true);
            Logger.d("onRecordChangedCurrentTime:" + DateUtil.formatDate(new Date(currentPlayingTime * 1000l), DateUtil.FORMAT_ONE));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    timeLineView.scrollTo(currentPlayingTime);
                }
            }, 300);

            List<CamDate> camDates = business.getDayTimeList(deviceId);
            for (int i = 0; i < camDates.size(); i++) {
                CamDate camDate = camDates.get(i);
                if (currentPlayingTime > camDate.getDayStartTime()) {
                    setDayText(i);
                    break;
                }
            }

            playRecord(currentPlayingTime);
            setThumbnailSelection();
            isPlayed = true;
        }
    }

    @Override
    public void onThumbnailChange() {
        List<CamThumbnail> thumbnails = business.getThumbnailList(deviceId);
        adapter.setThumbnails(thumbnails);
        adapter.notifyDataSetChanged();
        setThumbnailSelection();
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
        List<CamRecord> records = business.getRecordList(deviceId);
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

        List<CamDate> camDates = business.getDayTimeList(deviceId);
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
                viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.play_no_network), null);
            } else {
                if (autoRetryNum >= 2) {
                    autoRetryNum = 0;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.load_fail), null);
                } else {
                    autoRetryNum++;
                    viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.in_loading_please), null);
                    Logger.i("IErmuSDK.java", ">>>>>>> onError");
                    playRecord(currentPlayingTime);
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
//        Logger.i("onBufferingUpdate " + i);
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i2) {
        Logger.i("onInfo i=" + i + " i2=" + i2);
        switch (i) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START://第一帧上屏显示
                setEnable(true);
                if (isStopByClip && viewController.isPlaying()) {
                    changePlayState();
                }
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

        if (camLive.getConnectType() == ConnectType.LINYANG) {
            // 羚羊录像校正
            int llyTrueStartTime = (int) videoView.getBasepts();
            onVodSeek(currentPlayingStartTime, llyTrueStartTime, null, -1);
        }
    }

    @Override
    public void onOpenPlayRecord(RecordMedia media) {
        if (media == null
                || (media.getConnectRet() != 0 && media.getConnectType() == ConnectType.LINYANG)
                || TextUtils.isEmpty(media.getPlayUrl())) {
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.load_fail), null);
            return;
        }
        videoView.bufferON(true);
        videoView.setDelayMS(0);
        String playUrl = media.getPlayUrl();
        videoView.playVideo(playUrl, false);
    }

    @Override
    public void onVodSeek(int oldStartTime, int trueStartTime, Business business, int num) {
        if (business != null) {
            if (business.isSuccess()) {
                if (num == seekNum) {
                    int offect = trueStartTime - oldStartTime;
                    Logger.d("seekOk:" + offect);
                    currentPlayingTime += offect;
                    currentPlayingStartTime += offect;
                }
            } else {

            }
        } else {
            int offect = trueStartTime - oldStartTime;
            Logger.d("seekOk:" + offect);
            currentPlayingTime += offect;
            currentPlayingStartTime += offect;
        }
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
        isStopByClip = false;
        if (currentTimeTimer != null) {
            currentTimeTimer.cancel();
        }
        isOnScroll = false;
        List<CamDate> camDates = business.getDayTimeList(deviceId);
        for (int i = 0; i < camDates.size(); i++) {
            CamDate camDate = camDates.get(i);
            if (currentPlayingTime > camDate.getDayStartTime()) {
                Logger.d("dayStartTime:" + DateUtil.formatDate(new Date(camDate.getDayStartTime() * 1000l), DateUtil.FORMAT_ONE));
                setDayText(i);
                break;
            }
        }

        setThumbnailSelection();
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
        List<CamRecord> records = business.getRecordList(deviceId);
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
        ErmuBusiness.getStreamMediaBusiness().openPlayRecord(deviceId, startTime, endTime, record);
        if (camLive.getConnectType() == ConnectType.BAIDU) {
            startTime += DateUtil.TIME_ZONE_OFFECT;
            seekNum++;
            ErmuBusiness.getStreamMediaBusiness().vodSeek(deviceId, startTime, seekNum);
        }

        viewStatus.showViewStatus(PublicCamStatusView.STATUS_LOADING, camLive.getThumbnail(), getString(R.string.in_loading_please), null);
        setEnable(false);
        if (currentTimeTimer != null) {
            currentTimeTimer.cancel();
        }
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

    /**
     * 统一处理缩略图列表点击事件
     *
     * @param position
     */
    private void selectThumbnail(int position) {
        currentPlayingTime = adapter.getItem(position).getTime();
        List<CamDate> camDates = business.getDayTimeList(deviceId);
        for (int i = 0; i < camDates.size(); i++) {
            CamDate camDate = camDates.get(i);
            if (currentPlayingTime > camDate.getDayStartTime()) {
                Logger.d("dayStartTime:" + DateUtil.formatDate(new Date(camDate.getDayStartTime() * 1000l), DateUtil.FORMAT_ONE));
                setDayText(i);
                break;
            }
        }
        playRecord(currentPlayingTime);
        timeLineView.scrollTo(currentPlayingTime);
        adapter.setSelectedPosition(position);
    }

    /**
     * 缩略图跳到下一张
     */
    private void selectNextThumbnail() {
        currentThumbnailPosition++;
        adapter.setSelectedPosition(currentThumbnailPosition);
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
        isStopByClip = false;
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
            viewStatus.showViewStatus(PublicCamStatusView.STATUS_ERROR, null, getString(R.string.play_no_network), null);
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
        List<CamDate> camDates = business.getDayTimeList(deviceId);
        int dayEndTime = camDates.get(position).getDayStartTime() + DateUtil.DAY_SECOND_NUM;
        List<CamRecord> records = business.getRecordList(deviceId);
        CamRecord camRecord = null;
        for (int i = records.size() - 1; i >= 0; i--) {
            if (records.get(i).getStartTime() < dayEndTime) {
                camRecord = records.get(i);
                break;
            }
        }
        if (camRecord != null) {
            int endTime = camRecord.getEndTime();
            if (endTime > dayEndTime) {
                endTime = dayEndTime;
            }

            int startTime = camRecord.getStartTime();
            if (endTime - startTime > 900) {
                startTime = endTime - 900;
            }
            currentPlayingTime = startTime;
            timeLineView.scrollTo(currentPlayingTime);
            playRecord(currentPlayingTime);
            setThumbnailSelection();
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
     * 设置缩略图的选中
     */
    private void setThumbnailSelection() {
        List<CamThumbnail> thumbnails = business.getThumbnailList(deviceId);
        boolean isFind = false;
        for (int i = 0; i < thumbnails.size(); i++) {
            CamThumbnail camThumbnail = thumbnails.get(i);
            if (camThumbnail.getTime() > currentPlayingTime) {
                isFind = true;
                currentThumbnailPosition = i - 1;
                adapter.setSelectedPosition(currentThumbnailPosition);
                listViewTime.setSelection(currentThumbnailPosition);
                break;
            }
        }

        // 如果没找到对应缩略图，则使用最后最后一张缩略图
        if (!isFind && thumbnails != null && thumbnails.size() > 0) {
            currentThumbnailPosition = thumbnails.size() - 1;
            adapter.setSelectedPosition(currentThumbnailPosition);
            listViewTime.setSelection(currentThumbnailPosition);
        }
    }

    /**
     * 切换到时间轴模式
     */
    private void switchToTimeLine() {
        Animation animation = new TranslateAnimation(-Util.DensityUtil.dip2px(getActivity(), 30), Util.DensityUtil.dip2px(getActivity(), 30), 0, 0);
        animation.setDuration(300);
        animation.setFillAfter(true);
        viewBottomSwitch.startAnimation(animation);
        textViewTimeLine.setTextColor(0xff00acef);
        textViewThumbnail.setTextColor(0xffffffff);
        timeLineView.setVisibility(View.VISIBLE);
        listViewTime.setVisibility(View.INVISIBLE);
        if (isShowClip) {
            viewFilmEdit.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 切换到缩略图模式
     */
    private void switchToThumbnail() {
        Animation animation1 = new TranslateAnimation(Util.DensityUtil.dip2px(getActivity(), 30), -Util.DensityUtil.dip2px(getActivity(), 30), 0, 0);
        animation1.setDuration(300);
        animation1.setFillAfter(true);
        viewBottomSwitch.startAnimation(animation1);
        textViewTimeLine.setTextColor(0xffffffff);
        textViewThumbnail.setTextColor(0xff00acef);
        timeLineView.setVisibility(View.INVISIBLE);
        listViewTime.setVisibility(View.VISIBLE);
        viewFilmEdit.setVisibility(View.GONE);
    }

    /**
     * 刻画时间轴
     *
     * @param isLanscape
     */
    private void drawTimeLine(boolean isLanscape) {
        int windowWidth;
        if (isLanscape) {
            windowWidth = screenWidth > screenHeight ? screenWidth : screenHeight;
        } else {
            windowWidth = screenWidth > screenHeight ? screenHeight : screenWidth;
        }
        // 初始化时间轴
        long endTime = new Date().getTime() / 1000;
        long startTime = 0;
        List<CamRecord> records = business.getRecordList(deviceId);
        if (records != null && records.size() > 0) {
            startTime = records.get(0).getStartTime();
            int startTimehour = (int) (startTime / (ExpandTimeLineView.SECOND_PER_PIX * 300));
            startTime = startTimehour * (ExpandTimeLineView.SECOND_PER_PIX * 300);
        }
        timeLineView.draw(startTime, endTime, windowWidth, records);
    }

    @Override
    public void OnClipListener(Message msg) {
        switch (msg.what) {
            case 0:
                Logger.d("进度" + msg.obj);
                if (progressDialog != null) {
                    progressDialog.setProgress((Integer) msg.obj);
                }
                viewLittleProgress.setProgress((Integer) msg.obj);
                break;
            case 1:
                Logger.d("有进行中的任务");
                break;
            case 2:
                Logger.d("剪辑文件重名");
                break;
            case 3:
                isCliping = false;
                if (progressDialog != null) {
                    progressDialog.setProgress(100);
                }
                viewLittleProgress.setProgress(100);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (viewLittleProgress != null) {
                            viewLittleProgress.setVisibility(View.GONE);
                            textViewRecordCut.setText(getString(R.string.film_edit));
                        }

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                        if (getActivity() != null) {
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.view_record_cut_success_dialog);
                            dialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                }
                            }, 3000);
                        }
                    }
                }, 300);
                break;
            case -1:  // 剪辑err
                isCliping = false;
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                viewLittleProgress.setVisibility(View.GONE);
                textViewRecordCut.setText(getString(R.string.film_edit));
                showFilmEditFaildDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 获取当前播放时间
     */
    private class CurrentTimeTask extends TimerTask {
        @Override
        public void run() {
            if (!isOnScroll) {
                final long currentTime = currentPlayingStartTime * 1000l + videoView.getCurrentPosition();
                currentPlayingTime = (int) (currentTime / 1000);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeLineView.scrollTo(currentPlayingTime);
                            String dateStr = DateUtil.formatDate(new Date(currentTime), DateUtil.FORMAT_ONE);
                            viewController.setCurrentTime(dateStr, false);

                            // 自动跳转到下一个缩略图逻辑
                            if (currentThumbnailPosition < adapter.getCount() - 1) {
                                int currentThumbnailTime = adapter.getItem(currentThumbnailPosition + 1).getTime();
                                if (currentPlayingTime >= currentThumbnailTime) {
                                    selectNextThumbnail();
                                }
                            }
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
     * 弹出剪辑日期选择面板
     */
    private void showDateSelectDialog() {
        if (isCliping) {
            progressDialog.setButtonIKnowListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
            progressDialog.show();
        } else {
            if (viewController.isPlaying()) {
                changePlayState();
                isStopByClip = true;
            }
            selectDateDialog.setSelectTime(currentPlayingTime);
            selectDateDialog.setButtonEditListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int startTime = selectDateDialog.getCurrentSelectTime();
                    int endTime = startTime + selectDateDialog.getCurrentSelectMinute() * 60;
                    List<CamRecord> records = business.getRecordList(deviceId);
                    boolean isExistRecord = false;
                    for (int i = 0; i < records.size(); i++) {
                        CamRecord camRecord = records.get(i);
                        if (camRecord.getStartTime() > startTime && camRecord.getStartTime() < endTime
                                || camRecord.getEndTime() > startTime && camRecord.getEndTime() < endTime
                                || camRecord.getEndTime() > endTime && camRecord.getStartTime() < startTime) {
                            isExistRecord = true;
                            break;
                        }
                    }
                    if (isExistRecord) {
                        long clipStartTime = selectDateDialog.getCurrentSelectTime() + DateUtil.TIME_ZONE_OFFECT;
                        int clipMinute = selectDateDialog.getCurrentSelectMinute();
                        long clipEndTime = clipStartTime + clipMinute * 60;
                        Logger.d("clipStart:" + clipStartTime + "     endTime:" + clipEndTime);
                        preBusiness.setFilmEditTime(clipStartTime, clipEndTime);
                        business.startClipRec(clipStartTime, clipEndTime, deviceId, "iermu" + new Date().getTime());
                        isCliping = true;

                        selectDateDialog.dismiss();
                        viewLittleProgress.setVisibility(View.VISIBLE);
                        viewLittleProgress.setProgress(0);
                        textViewRecordCut.setText(getString(R.string.clip_fail_now));
                        progressDialog = new FilmEditProgressDialog(getActivity());
                        progressDialog.setProgress(0);
                        progressDialog.setButtonIKnowListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressDialog.dismiss();
                            }
                        });
                        progressDialog.show();
                    } else {
                        selectDateDialog.showToast(new Handler());
                    }
                }
            });
            selectDateDialog.setButtonCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectDateDialog.dismiss();
                }
            });
            selectDateDialog.show();
        }
    }

    /**
     * 显示剪辑失败弹出框
     */
    private void showFilmEditFaildDialog() {
        isFilmEditFaild = false;
        preBusiness.setFilmEditIsFaild(false);
        failDialog = new FilmEditFailDialog(getActivity());
        failDialog.setButtonTryAgainListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failDialog.dismiss();
                isCliping = true;
                viewLittleProgress.setVisibility(View.VISIBLE);
                viewLittleProgress.setProgress(0);
                textViewRecordCut.setText(getString(R.string.clip_fail_now));
                progressDialog = new FilmEditProgressDialog(getActivity());
                progressDialog.setProgress(0);
                progressDialog.setButtonIKnowListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.dismiss();
                    }
                });
                progressDialog.show();
                long clipStartTime = preBusiness.getFilmEditStartTime();
                long clipEndTime = preBusiness.getFilmEditEndTime();
                Logger.d("clipStart:" + clipStartTime + "     endTime:" + clipEndTime);
                business.startClipRec(clipStartTime, clipEndTime, deviceId, "iermu" + new Date().getTime());
            }
        });
        failDialog.setButtonCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failDialog.dismiss();
            }
        });
        failDialog.show();
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
     * 文案前加圆点
     */
    private void showCircle(TextView view, String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new BulletSpan(Util.DensityUtil.dip2px(getActivity(), 4), Color.parseColor("#ff888888")), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(ssb);
    }
}
